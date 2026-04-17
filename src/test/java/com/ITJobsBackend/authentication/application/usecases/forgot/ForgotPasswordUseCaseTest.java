package com.ITJobsBackend.authentication.application.usecases.forgot;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.event.PasswordResetRequestedEvent;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.PasswordResetToken;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.application.ports.out.DomainEventPublisher;
import com.ITJobsBackend.shared.domain.event.DomainEvent;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordUseCaseTest {

  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String EMAIL = "john@example.com";
  private static final String UNKNOWN_EMAIL = "nonexistent@example.com";

  @Mock private LoadUserPort loadUserPort;
  @Mock private DomainEventPublisher domainEventPublisher;

  @InjectMocks private ForgotPasswordUseCase useCase;

  private UserAggregate buildUser() {
    return UserAggregate.reconstitute(
        UserId.of(USER_ID),
        Username.of("john"),
        Email.of(EMAIL),
        HashedPassword.fromHash("$2a$10$hashed"),
        true,
        true,
        null,
        Timestamp.now(),
        Timestamp.now(),
        List.of("ROLE_USER"),
        null);
  }

  @Test
  void shouldGenerateResetTokenForExistingUser() {
    given(loadUserPort.findByEmail(Email.of(EMAIL))).willReturn(Optional.of(buildUser()));
    willDoNothing().given(domainEventPublisher).publishAll(any());

    useCase.execute(new ForgotPasswordCommand(EMAIL));

    then(loadUserPort).should().findByEmail(Email.of(EMAIL));
    then(domainEventPublisher).should().publishAll(any());
  }

  @Test
  void shouldDoNothingWhenUserNotFound() {
    given(loadUserPort.findByEmail(Email.of(UNKNOWN_EMAIL))).willReturn(Optional.empty());

    assertDoesNotThrow(() -> useCase.execute(new ForgotPasswordCommand(UNKNOWN_EMAIL)));
    then(domainEventPublisher).should(never()).publishAll(any());
  }

  @SuppressWarnings("unchecked")
  @Test
  void shouldPublishEventWithCorrectData() {
    given(loadUserPort.findByEmail(Email.of(EMAIL))).willReturn(Optional.of(buildUser()));

    useCase.execute(new ForgotPasswordCommand(EMAIL));

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
