package com.risetogether.authentication.infrastructure.events;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.risetogether.authentication.application.ports.out.LoadUserPort;
import com.risetogether.authentication.application.ports.out.SaveUserPort;
import com.risetogether.authentication.application.ports.out.VerificationTokenExpirationPort;
import com.risetogether.authentication.domain.aggregate.UserAggregate;
import com.risetogether.authentication.domain.event.UserRegisteredEvent;
import com.risetogether.authentication.domain.valueobjects.HashedPassword;
import com.risetogether.authentication.domain.valueobjects.Username;
import com.risetogether.shared.domain.valueobjects.Email;
import com.risetogether.shared.domain.valueobjects.Timestamp;
import com.risetogether.shared.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class UserRegisteredEventListenerTest {

  // ── Constants ─────────────────────────────────────────────────────────────
  private static final String EMAIL = "test@example.com";
  private static final String USERNAME = "testuser";
  private static final String HASHED_PASSWORD = "hashed";

  // ── Mocks (solo puertos de salida) ────────────────────────────────────────
  @Mock private LoadUserPort loadUserPort;
  @Mock private SaveUserPort saveUserPort;
  @Mock private VerificationTokenExpirationPort verificationTokenExpirationPort;

  // ── Subject under test ────────────────────────────────────────────────────
  @InjectMocks private UserRegisteredEventListener listener;

  // ── Helpers ───────────────────────────────────────────────────────────────
  private UserAggregate buildUser(UserId userId) {
    return UserAggregate.reconstitute(
        userId,
        Username.of(USERNAME),
        Email.of(EMAIL),
        HashedPassword.fromHash(HASHED_PASSWORD),
        false,
        false,
        null,
        Timestamp.now(),
        Timestamp.now(),
        List.of(),
        null);
  }

  // ── Tests ─────────────────────────────────────────────────────────────────
  @Test
  void shouldGenerateVerificationTokenWithConfiguredExpiration() {
    // Given
    UserId userId = UserId.generate();
    UserAggregate user = buildUser(userId);
    given(verificationTokenExpirationPort.getVerificationTokenExpirationHours()).willReturn(12);
    given(loadUserPort.findById(userId)).willReturn(Optional.of(user));
    given(saveUserPort.save(any())).willAnswer(invocation -> invocation.getArgument(0));

    // When
    listener.on(new UserRegisteredEvent(userId, Email.of(EMAIL)));

    // Then
    then(saveUserPort).should().save(any(UserAggregate.class));
    then(loadUserPort).should().findById(userId);
  }

  @Test
  void shouldAssignTokenExpiringInConfiguredHours() {
    // Given
    UserId userId = UserId.generate();
    UserAggregate user = buildUser(userId);
    given(verificationTokenExpirationPort.getVerificationTokenExpirationHours()).willReturn(12);
    given(loadUserPort.findById(userId)).willReturn(Optional.of(user));
    given(saveUserPort.save(any())).willAnswer(invocation -> invocation.getArgument(0));

    // When
    listener.on(new UserRegisteredEvent(userId, Email.of(EMAIL)));

    // Then
    var token = user.getVerificationToken();
    assertNotNull(token, "Verification token should be assigned");
    Instant expectedExpires = Instant.now().plusSeconds(12 * 3600L);
    long diffSeconds =
        Math.abs(expectedExpires.getEpochSecond() - token.expiresAt().getEpochSecond());
    assertTrue(diffSeconds < 5, "Token should expire in approximately 12 hours");
  }

  @Test
  void shouldAssignTokenExpiringInOneHourWhenConfiguredAccordingly() {
    // Given
    UserId userId = UserId.generate();
    UserAggregate user = buildUser(userId);
    given(verificationTokenExpirationPort.getVerificationTokenExpirationHours()).willReturn(1);
    given(loadUserPort.findById(userId)).willReturn(Optional.of(user));
    given(saveUserPort.save(any())).willAnswer(invocation -> invocation.getArgument(0));

    // When
    listener.on(new UserRegisteredEvent(userId, Email.of(EMAIL)));

    // Then
    var token = user.getVerificationToken();
    assertNotNull(token, "Verification token should be assigned");
    Instant expectedExpires = Instant.now().plusSeconds(3600);
    long diffSeconds =
        Math.abs(expectedExpires.getEpochSecond() - token.expiresAt().getEpochSecond());
    assertTrue(diffSeconds < 5, "Token should expire in 1 hour");
  }
}
