package com.ITJobsBackend.authentication.application.usecases.verify;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.application.ports.out.VerificationTokenValidatorPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.exceptions.EmailAlreadyVerifiedException;
import com.ITJobsBackend.authentication.domain.exceptions.VerificationTokenExpiredException;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.authentication.domain.valueobjects.VerificationToken;
import com.ITJobsBackend.shared.application.ports.out.DomainEventPublisher;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class VerifyEmailUseCaseTest {

  // ── Constants ─────────────────────────────────────────────────────────────
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String EMAIL = "john@example.com";
  private static final String TOKEN = "verification-token";

  // ── Mocks (puertos de salida) ──────────────────────────────────────────────
  @Mock private LoadUserPort loadUserPort;
  @Mock private SaveUserPort saveUserPort;
  @Mock private DomainEventPublisher domainEventPublisher;
  @Mock private VerificationTokenValidatorPort verificationTokenValidatorPort;

  // ── Subject under test ────────────────────────────────────────────────────
  @InjectMocks private VerifyEmailUseCase useCase;

  // ── Shared command ────────────────────────────────────────────────────────
  private VerifyEmailCommand command;

  @BeforeEach
  void setUp() {
    command = new VerifyEmailCommand(USER_ID, TOKEN);
  }

  // ── Helpers ───────────────────────────────────────────────────────────────
  private UserAggregate buildUser(boolean active, boolean emailVerified) {
    return buildUser(active, emailVerified, TOKEN, Instant.now().plus(24, ChronoUnit.HOURS));
  }

  private UserAggregate buildUser(
      boolean active, boolean emailVerified, String verificationToken, Instant expiresAt) {
    VerificationToken token =
        verificationToken != null ? VerificationToken.of(verificationToken, expiresAt) : null;
    return UserAggregate.reconstitute(
        UserId.of(USER_ID),
        Username.of("john"),
        Email.of(EMAIL),
        HashedPassword.fromHash("$2a$10$hashed"),
        active,
        emailVerified,
        null,
        Timestamp.now(),
        Timestamp.now(),
        List.of("ROLE_USER"),
        token);
  }

  // ── Tests ─────────────────────────────────────────────────────────────────
  @Test
  void shouldVerifyEmailSuccessfully() {
    // Given
    UserAggregate user = buildUser(false, false);
    given(loadUserPort.findById(UserId.of(USER_ID))).willReturn(Optional.of(user));

    // When
    useCase.execute(command);

    // Then
    assertTrue(user.isEmailVerified());
    then(saveUserPort).should().save(user);
    then(domainEventPublisher).should().publishAll(any());
    then(verificationTokenValidatorPort).should().validate(user.getVerificationToken(), TOKEN);
  }

  @Test
  void shouldThrowExceptionWhenEmailAlreadyVerified() {
    // Given
    UserAggregate user = buildUser(true, true, TOKEN, Instant.now().plus(24, ChronoUnit.HOURS));
    given(loadUserPort.findById(UserId.of(USER_ID))).willReturn(Optional.of(user));

    // When & Then
    assertThrows(EmailAlreadyVerifiedException.class, () -> useCase.execute(command));
    then(saveUserPort).should(never()).save(any(UserAggregate.class));
    then(domainEventPublisher).should(never()).publishAll(any());
  }

  @Test
  void shouldThrowExceptionWhenVerificationTokenExpired() {
    // Given
    UserAggregate user = buildUser(false, false, TOKEN, Instant.now().minus(1, ChronoUnit.HOURS));
    given(loadUserPort.findById(UserId.of(USER_ID))).willReturn(Optional.of(user));
    willThrow(new VerificationTokenExpiredException("Verification token has expired"))
        .given(verificationTokenValidatorPort)
        .validate(any(), eq(TOKEN));

    // When & Then
    assertThrows(VerificationTokenExpiredException.class, () -> useCase.execute(command));
    then(saveUserPort).should(never()).save(any(UserAggregate.class));
    then(domainEventPublisher).should(never()).publishAll(any());
  }

  @Test
  void shouldThrowExceptionWhenVerificationTokenInvalid() {
    // Given
    UserAggregate user =
        buildUser(false, false, "different-token", Instant.now().plus(24, ChronoUnit.HOURS));
    given(loadUserPort.findById(UserId.of(USER_ID))).willReturn(Optional.of(user));
    willThrow(new IllegalArgumentException("Invalid verification token"))
        .given(verificationTokenValidatorPort)
        .validate(any(), eq(TOKEN));

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
    then(saveUserPort).should(never()).save(any(UserAggregate.class));
    then(domainEventPublisher).should(never()).publishAll(any());
  }

  @Test
  void shouldThrowExceptionWhenUserNotFound() {
    // Given
    given(loadUserPort.findById(UserId.of(USER_ID))).willReturn(Optional.empty());

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
    then(saveUserPort).should(never()).save(any(UserAggregate.class));
    then(domainEventPublisher).should(never()).publishAll(any());
  }
}
