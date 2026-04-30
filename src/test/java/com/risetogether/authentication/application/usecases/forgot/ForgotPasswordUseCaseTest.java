package com.risetogether.authentication.application.usecases.forgot;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.risetogether.authentication.application.ports.out.QueryUserPort;
import com.risetogether.authentication.application.query.UserView;
import com.risetogether.authentication.domain.event.PasswordResetRequestedEvent;
import com.risetogether.authentication.domain.valueobjects.HashedPassword;
import com.risetogether.authentication.domain.valueobjects.PasswordResetToken;
import com.risetogether.shared.application.ports.out.DomainEventPublisher;
import com.risetogether.shared.domain.event.DomainEvent;
import com.risetogether.shared.domain.valueobjects.Email;
import com.risetogether.shared.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordUseCaseTest {

  // ── Constants ─────────────────────────────────────────────────────────────
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String EMAIL = "john@example.com";
  private static final String HASHED_PASSWORD = "$2a$10$hashed";
  private static final String UNKNOWN_EMAIL = "nonexistent@example.com";

  // ── Mocks (puertos de salida) ──────────────────────────────────────────────
  @Mock private QueryUserPort queryUserPort;
  @Mock private DomainEventPublisher domainEventPublisher;

  // ── Subject under test ────────────────────────────────────────────────────
  @InjectMocks private ForgotPasswordUseCase useCase;

  // ── Helpers ───────────────────────────────────────────────────────────────
  private UserView buildUserView() {
    return new UserView(
        UserId.of(USER_ID),
        "john",
        EMAIL,
        HashedPassword.fromHash(HASHED_PASSWORD),
        true,
        true,
        List.of("ROLE_USER"));
  }

  // ── Tests ─────────────────────────────────────────────────────────────────
  @Test
  void shouldGenerateResetTokenForExistingUser() {
    // Given
    given(queryUserPort.findByEmail(Email.of(EMAIL))).willReturn(Optional.of(buildUserView()));

    // When
    useCase.execute(new ForgotPasswordCommand(EMAIL));

    // Then
    then(queryUserPort).should().findByEmail(Email.of(EMAIL));
    then(domainEventPublisher).should().publishAll(any());
  }

  @Test
  void shouldDoNothingWhenUserNotFound() {
    // Given
    given(queryUserPort.findByEmail(Email.of(UNKNOWN_EMAIL))).willReturn(Optional.empty());

    // When & Then
    assertDoesNotThrow(() -> useCase.execute(new ForgotPasswordCommand(UNKNOWN_EMAIL)));
    then(domainEventPublisher).should(never()).publishAll(any());
  }

  @SuppressWarnings("unchecked")
  @Test
  void shouldPublishEventWithCorrectData() {
    // Given
    given(queryUserPort.findByEmail(Email.of(EMAIL))).willReturn(Optional.of(buildUserView()));

    // When
    useCase.execute(new ForgotPasswordCommand(EMAIL));

    // Then
    ArgumentCaptor<List<DomainEvent>> captor = ArgumentCaptor.forClass(List.class);
    then(domainEventPublisher).should().publishAll(captor.capture());

    List<DomainEvent> published = captor.getValue();
    assertEquals(1, published.size());

    PasswordResetRequestedEvent event = (PasswordResetRequestedEvent) published.get(0);
    assertEquals(USER_ID, event.getAggregateId());
    assertEquals(Email.of(EMAIL), event.getEmail());

    PasswordResetToken passwordResetToken = event.getPasswordResetToken();
    assertNotNull(passwordResetToken);
    assertNotNull(passwordResetToken.value());
    assertFalse(passwordResetToken.isExpired());
  }
}
