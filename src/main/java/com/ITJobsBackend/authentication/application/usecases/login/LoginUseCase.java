package com.ITJobsBackend.authentication.application.usecases.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ITJobsBackend.authentication.application.ports.in.LoginPort;
import com.ITJobsBackend.authentication.application.ports.out.PasswordEncoderPort;
import com.ITJobsBackend.authentication.application.ports.out.QueryUserPort;
import com.ITJobsBackend.authentication.application.ports.out.TokenGeneratorPort;
import com.ITJobsBackend.authentication.application.query.UserView;
import com.ITJobsBackend.authentication.domain.exceptions.InvalidCredentialsException;
import com.ITJobsBackend.authentication.domain.service.CredentialsVerifier;
import com.ITJobsBackend.shared.domain.valueobjects.Email;

@Service
@Transactional(readOnly = true)
public class LoginUseCase implements LoginPort {
  private static final Logger log = LoggerFactory.getLogger(LoginUseCase.class);

  private final QueryUserPort queryUserPort;
  private final PasswordEncoderPort passwordEncoder;
  private final TokenGeneratorPort tokenGenerator;
  private final CredentialsVerifier credentialsVerifier;

  public LoginUseCase(
      QueryUserPort queryUserPort,
      PasswordEncoderPort passwordEncoder,
      TokenGeneratorPort tokenGenerator,
      CredentialsVerifier credentialsVerifier) {
    this.queryUserPort = queryUserPort;
    this.passwordEncoder = passwordEncoder;
    this.tokenGenerator = tokenGenerator;
    this.credentialsVerifier = credentialsVerifier;
  }

  @Override
  public AuthTokenResponse execute(LoginCommand command) {
    log.info("Login attempt for email: {}", command.email());

    Email email = Email.of(command.email());

    UserView user = queryUserPort.findByEmail(email).orElseThrow(InvalidCredentialsException::new);

    credentialsVerifier.verifyCredentials(
        user.hashedPassword(), command.password(), passwordEncoder);

    String accessToken =
        tokenGenerator.generateAccessToken(user.id().value().toString(), user.roles());
    String refreshToken = tokenGenerator.generateRefreshToken(user.id().value().toString());

    log.info("User logged in successfully: {}", user.id());

    return new AuthTokenResponse(
        user.id().value().toString(), user.username(), user.email(), accessToken, refreshToken);
  }
}
