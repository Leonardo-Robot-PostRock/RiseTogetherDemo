package com.ITJobsBackend.authentication.application.usecases.login;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.application.ports.out.PasswordEncoderPort;
import com.ITJobsBackend.authentication.application.ports.out.TokenGeneratorPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.exceptions.InvalidCredentialsException;
import com.ITJobsBackend.authentication.domain.service.CredentialsVerifier;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.domain.valueobjects.Email;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

  private static final String EMAIL = "john@example.com";
  private static final String HASHED_PASSWORD = "$2a$10$hashed";

  @Mock private LoadUserPort loadUserPort;
  @Mock private PasswordEncoderPort passwordEncoder;
  @Mock private TokenGeneratorPort tokenGenerator;

  private LoginUseCase loginUseCase;

  @BeforeEach
  void setUp() {
    loginUseCase =
        new LoginUseCase(loadUserPort, passwordEncoder, tokenGenerator, new CredentialsVerifier());
  }

  private UserAggregate buildUser() {
    return UserAggregate.create(
        Username.of("johndoe"), Email.of(EMAIL), HashedPassword.fromHash(HASHED_PASSWORD));
  }

  private void givenTokensAreStubbed() {
    when(tokenGenerator.generateAccessToken(any(), any())).thenReturn("access-token");
    when(tokenGenerator.generateRefreshToken(any())).thenReturn("refresh-token");
  }

  @Test
  void shouldLoginSuccessfully() {
    when(loadUserPort.findByEmail(any(Email.class))).thenReturn(Optional.of(buildUser()));
    when(passwordEncoder.matches("password123", HASHED_PASSWORD)).thenReturn(true);
    givenTokensAreStubbed();

    AuthTokenResponse response = loginUseCase.execute(new LoginCommand(EMAIL, "password123"));

    assertNotNull(response.accessToken());
    assertNotNull(response.refreshToken());
  }

  @Test
  void shouldThrowWhenUserNotFound() {
    when(loadUserPort.findByEmail(any(Email.class))).thenReturn(Optional.empty());

    assertThrows(
        InvalidCredentialsException.class,
        () -> loginUseCase.execute(new LoginCommand("nobody@example.com", "password123")));
  }

  @Test
  void shouldPassRawPasswordWithoutValidation() {
    when(loadUserPort.findByEmail(any(Email.class))).thenReturn(Optional.of(buildUser()));
    when(passwordEncoder.matches("short", HASHED_PASSWORD)).thenReturn(true);
    givenTokensAreStubbed();

    loginUseCase.execute(new LoginCommand(EMAIL, "short"));
  }
}
