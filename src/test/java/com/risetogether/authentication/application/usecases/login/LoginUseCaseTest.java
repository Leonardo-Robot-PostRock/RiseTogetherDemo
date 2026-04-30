package com.risetogether.authentication.application.usecases.login;

import java.util.List;
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

import com.risetogether.authentication.application.ports.out.PasswordEncoderPort;
import com.risetogether.authentication.application.ports.out.QueryUserPort;
import com.risetogether.authentication.application.ports.out.TokenGeneratorPort;
import com.risetogether.authentication.application.query.UserView;
import com.risetogether.authentication.domain.exceptions.InvalidCredentialsException;
import com.risetogether.authentication.domain.service.CredentialsVerifier;
import com.risetogether.authentication.domain.valueobjects.HashedPassword;
import com.risetogether.shared.domain.valueobjects.Email;
import com.risetogether.shared.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

  // ── Constants ─────────────────────────────────────────────────────────────
  private static final String EMAIL = "john@example.com";
  private static final String HASHED_PASSWORD = "$2a$10$hashed";

  // ── Mocks (puertos de salida) ──────────────────────────────────────────────
  @Mock private QueryUserPort queryUserPort;
  @Mock private PasswordEncoderPort passwordEncoder;
  @Mock private TokenGeneratorPort tokenGenerator;

  // ── Domain service (instancia real) ───────────────────────────────────────
  @Spy private CredentialsVerifier credentialsVerifier;

  // ── Subject under test ────────────────────────────────────────────────────
  @InjectMocks private LoginUseCase loginUseCase;

  // ── Helpers ───────────────────────────────────────────────────────────────
  private UserView buildUserView() {
    return new UserView(
        UserId.generate(),
        "johndoe",
        EMAIL,
        HashedPassword.fromHash(HASHED_PASSWORD),
        true,
        true,
        List.of("ROLE_USER"));
  }

  private void givenTokensAreStubbed() {
    given(tokenGenerator.generateAccessToken(any(), any())).willReturn("access-token");
    given(tokenGenerator.generateRefreshToken(any())).willReturn("refresh-token");
  }

  // ── Tests ─────────────────────────────────────────────────────────────────
  @Test
  void shouldLoginSuccessfully() {
    // Given
    given(queryUserPort.findByEmail(any(Email.class))).willReturn(Optional.of(buildUserView()));
    given(passwordEncoder.matches("password123", HASHED_PASSWORD)).willReturn(true);
    givenTokensAreStubbed();

    // When
    AuthTokenResponse response = loginUseCase.execute(new LoginCommand(EMAIL, "password123"));

    // Then
    assertNotNull(response.accessToken());
    assertNotNull(response.refreshToken());
    then(queryUserPort).should().findByEmail(any(Email.class));
  }

  @Test
  void shouldThrowWhenUserNotFound() {
    // Given
    given(queryUserPort.findByEmail(any(Email.class))).willReturn(Optional.empty());

    // When & Then
    assertThrows(
        InvalidCredentialsException.class,
        () -> loginUseCase.execute(new LoginCommand("nobody@example.com", "password123")));
  }

  @Test
  void shouldThrowWhenPasswordIsIncorrect() {
    // Given
    given(queryUserPort.findByEmail(any(Email.class))).willReturn(Optional.of(buildUserView()));
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
    given(queryUserPort.findByEmail(any(Email.class))).willReturn(Optional.of(buildUserView()));
    given(passwordEncoder.matches("short", HASHED_PASSWORD)).willReturn(true);
    givenTokensAreStubbed();

    // When & Then — short password is accepted (validation happens on raw Password VO, not here)
    assertDoesNotThrow(() -> loginUseCase.execute(new LoginCommand(EMAIL, "short")));
  }
}
