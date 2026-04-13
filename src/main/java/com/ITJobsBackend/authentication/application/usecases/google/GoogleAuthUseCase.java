package com.ITJobsBackend.authentication.application.usecases.google;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ITJobsBackend.authentication.application.ports.in.GoogleAuthPort;
import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.application.ports.out.TokenGeneratorPort;
import com.ITJobsBackend.authentication.application.usecases.login.AuthTokenResponse;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.exceptions.InvalidCredentialsException;
import com.ITJobsBackend.authentication.domain.valueobjects.GoogleSub;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.domain.valueobjects.Email;

@Service
@Transactional
public class GoogleAuthUseCase implements GoogleAuthPort {
  private static final Logger log = LoggerFactory.getLogger(GoogleAuthUseCase.class);

  private final LoadUserPort loadUserPort;
  private final SaveUserPort saveUserPort;
  private final TokenGeneratorPort tokenGenerator;

  public GoogleAuthUseCase(
      LoadUserPort loadUserPort, SaveUserPort saveUserPort, TokenGeneratorPort tokenGenerator) {
    this.loadUserPort = loadUserPort;
    this.saveUserPort = saveUserPort;
    this.tokenGenerator = tokenGenerator;
  }

  @Override
  public AuthTokenResponse execute(GoogleAuthCommand command) {
    log.info("Google authentication attempt for email: {}", command.email());

    Email email = Email.of(command.email());
    GoogleSub googleSub = GoogleSub.of(command.googleSub());

    Optional<UserAggregate> existingUser = loadUserPort.findByEmail(email);

    // If a user with the same email exists, ensure the Google sub matches
    existingUser.ifPresent(
        u -> {
          GoogleSub storedSub = u.getGoogleSub();
          if (storedSub != null && !storedSub.value().equals(googleSub.value())) {
            throw new InvalidCredentialsException();
          }
        });

    UserAggregate user =
        existingUser.orElseGet(() -> createNewGoogleUser(googleSub, email, command.name()));

    String accessToken =
        tokenGenerator.generateAccessToken(user.getId().value().toString(), user.getRoles());
    String refreshToken = tokenGenerator.generateRefreshToken(user.getId().value().toString());

    log.info("Google user authenticated successfully: {}", user.getId());

    return new AuthTokenResponse(
        user.getId().value().toString(),
        user.getUsername().value(),
        user.getEmail().value(),
        accessToken,
        refreshToken);
  }

  private UserAggregate createNewGoogleUser(GoogleSub googleSub, Email email, String name) {
    log.info("Creating new Google user with sub: {}", googleSub);

    Username username = Username.of(name != null ? name : email.value().split("@")[0]);
    HashedPassword password = HashedPassword.fromHash(UUID.randomUUID().toString());

    UserAggregate user = UserAggregate.createGoogleUser(username, email, password, googleSub);
    return saveUserPort.save(user);
  }
}
