package com.ITJobsBackend.authentication.application.usecases.login;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
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
    given(tokenGenerator.generateAccessToken(any(), any())).willReturn("access-token");
    given(tokenGenerator.generateRefreshToken(any())).willReturn("refresh-token");
  }

  @Test
  void shouldLoginSuccessfully() {
    // Given
    given(loadUserPort.findByEmail(any(Email.class))).willReturn(Optional.of(buildUser()));
    given(passwordEncoder.matches("password123", HASHED_PASSWORD)).willReturn(true);
    givenTokensAreStubbed();

    // When
    AuthTokenResponse response = loginUseCase.execute(new LoginCommand(EMAIL, "password123"));

    // Then
    assertNotNull(response.accessToken());
    assertNotNull(response.refreshToken());
    then(loadUserPort).should().findByEmail(any(Email.class));
  }

  @Test
  void shouldThrowWhenUserNotFound() {
    // Given
    given(loadUserPort.findByEmail(any(Email.class))).willReturn(Optional.empty());

    // When & Then
    assertThrows(
        InvalidCredentialsException.class,
        () -> loginUseCase.execute(new LoginCommand("nobody@example.com", "password123")));
  }

  @Test
  void shouldPassRawPasswordWithoutValidation() {
    // Given
    given(loadUserPort.findByEmail(any(Email.class))).willReturn(Optional.of(buildUser()));
    given(passwordEncoder.matches("short", HASHED_PASSWORD)).willReturn(true);
    givenTokensAreStubbed();

    // When (no exception thrown for short password — validation is on raw Password VO, not here)
    loginUseCase.execute(new LoginCommand(EMAIL, "short"));
  }
}
