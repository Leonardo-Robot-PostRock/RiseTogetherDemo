package com.ITJobsBackend.authentication.application.usecases.login;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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

  // ── Constants ─────────────────────────────────────────────────────────────
  private static final String EMAIL = "john@example.com";
  private static final String HASHED_PASSWORD = "$2a$10$hashed";

  // ── Mocks (puertos de salida) ──────────────────────────────────────────────
  @Mock private LoadUserPort loadUserPort;
  @Mock private PasswordEncoderPort passwordEncoder;
  @Mock private TokenGeneratorPort tokenGenerator;

  // ── Domain service (instancia real) ───────────────────────────────────────
  @Spy private CredentialsVerifier credentialsVerifier;

  // ── Subject under test ────────────────────────────────────────────────────
  @InjectMocks private LoginUseCase loginUseCase;

  // ── Helpers ───────────────────────────────────────────────────────────────
  private UserAggregate buildUser() {
    return UserAggregate.create(
        Username.of("johndoe"), Email.of(EMAIL), HashedPassword.fromHash(HASHED_PASSWORD));
  }

  private void givenTokensAreStubbed() {
    given(tokenGenerator.generateAccessToken(any(), any())).willReturn("access-token");
    given(tokenGenerator.generateRefreshToken(any())).willReturn("refresh-token");
  }

  // ── Tests ─────────────────────────────────────────────────────────────────
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
  void shouldThrowWhenPasswordIsIncorrect() {
    // Given
    given(loadUserPort.findByEmail(any(Email.class))).willReturn(Optional.of(buildUser()));
    given(passwordEncoder.matches("wrongPass", HASHED_PASSWORD)).willReturn(false);

    // When & Then
    assertThrows(
        InvalidCredentialsException.class,
        () -> loginUseCase.execute(new LoginCommand(EMAIL, "wrongPass")));
    then(tokenGenerator).shouldHaveNoInteractions();
  }

  @Test
  void shouldPassRawPasswordWithoutValidation() {
    // Given
    given(loadUserPort.findByEmail(any(Email.class))).willReturn(Optional.of(buildUser()));
    given(passwordEncoder.matches("short", HASHED_PASSWORD)).willReturn(true);
    givenTokensAreStubbed();

    // When & Then — short password is accepted (validation happens on raw Password VO, not here)
    assertDoesNotThrow(() -> loginUseCase.execute(new LoginCommand(EMAIL, "short")));
  }
}
