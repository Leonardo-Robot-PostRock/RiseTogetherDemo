package com.risetogether.authentication.application.usecases.google;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.risetogether.authentication.application.ports.in.GoogleAuthPort;
import com.risetogether.authentication.application.ports.out.LoadTermsDocumentPort;
import com.risetogether.authentication.application.ports.out.LoadUserPort;
import com.risetogether.authentication.application.ports.out.SaveTermsAcceptancePort;
import com.risetogether.authentication.application.ports.out.SaveUserPort;
import com.risetogether.authentication.application.ports.out.TokenGeneratorPort;
import com.risetogether.authentication.application.usecases.login.AuthTokenResponse;
import com.risetogether.authentication.domain.aggregate.UserAggregate;
import com.risetogether.authentication.domain.entity.TermsAcceptance;
import com.risetogether.authentication.domain.exceptions.InvalidCredentialsException;
import com.risetogether.authentication.domain.exceptions.TermsDocumentNotFoundException;
import com.risetogether.authentication.domain.valueobjects.GoogleSub;
import com.risetogether.authentication.domain.valueobjects.HashedPassword;
import com.risetogether.authentication.domain.valueobjects.TermsType;
import com.risetogether.authentication.domain.valueobjects.Username;
import com.risetogether.shared.domain.valueobjects.Email;

@Service
@Transactional
public class GoogleAuthUseCase implements GoogleAuthPort {
  private static final Logger log = LoggerFactory.getLogger(GoogleAuthUseCase.class);

  private final LoadUserPort loadUserPort;
  private final SaveUserPort saveUserPort;
  private final TokenGeneratorPort tokenGenerator;
  private final LoadTermsDocumentPort loadTermsDocumentPort;
  private final SaveTermsAcceptancePort saveTermsAcceptancePort;

  public GoogleAuthUseCase(
      LoadUserPort loadUserPort,
      SaveUserPort saveUserPort,
      TokenGeneratorPort tokenGenerator,
      LoadTermsDocumentPort loadTermsDocumentPort,
      SaveTermsAcceptancePort saveTermsAcceptancePort) {
    this.loadUserPort = loadUserPort;
    this.saveUserPort = saveUserPort;
    this.tokenGenerator = tokenGenerator;
    this.loadTermsDocumentPort = loadTermsDocumentPort;
    this.saveTermsAcceptancePort = saveTermsAcceptancePort;
  }

  @Override
  public AuthTokenResponse execute(GoogleAuthCommand command) {
    log.info("Google authentication attempt for email: {}", command.email());

    if (!command.termsAccepted()) {
      throw new IllegalArgumentException("Terms and conditions must be accepted");
    }

    Email email = Email.of(command.email());
    GoogleSub googleSub = GoogleSub.of(command.googleSub());

    Optional<UserAggregate> existingUser = loadUserPort.findByEmail(email);

    existingUser.ifPresent(
        u -> {
          GoogleSub storedSub = u.getGoogleSub();
          if (storedSub != null && !storedSub.value().equals(googleSub.value())) {
            throw new InvalidCredentialsException();
          }
        });

    boolean isNewUser = existingUser.isEmpty();
    UserAggregate user =
        existingUser.orElseGet(() -> createNewGoogleUser(googleSub, email, command.name()));

    if (isNewUser) {
      recordTermsAcceptance(user, TermsType.TERMS_OF_SERVICE);
    }

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

  private void recordTermsAcceptance(UserAggregate user, TermsType termsType) {
    var termsDocument =
        loadTermsDocumentPort
            .findLatestByType(termsType)
            .orElseThrow(() -> new TermsDocumentNotFoundException(termsType.name()));
    saveTermsAcceptancePort.save(TermsAcceptance.create(user.getId(), termsDocument.getId()));
  }
}
