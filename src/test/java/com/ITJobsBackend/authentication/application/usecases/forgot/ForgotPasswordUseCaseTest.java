package com.ITJobsBackend.authentication.application.usecases.forgot;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.application.ports.out.DomainEventPublisher;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordUseCaseTest {

  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String EMAIL = "john@example.com";

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
        List.of("ROLE_USER"));
  }

  @Test
  void shouldGenerateResetTokenForExistingUser() {
    when(loadUserPort.findByEmail(Email.of(EMAIL))).thenReturn(Optional.of(buildUser()));
    doNothing().when(domainEventPublisher).publishAll(any());

    useCase.execute(new ForgotPasswordCommand(EMAIL));

    verify(loadUserPort).findByEmail(Email.of(EMAIL));
    verify(domainEventPublisher).publishAll(any());
  }

  @Test
  void shouldDoNothingWhenUserNotFound() {
    String unknownEmail = "nonexistent@example.com";
    when(loadUserPort.findByEmail(Email.of(unknownEmail))).thenReturn(Optional.empty());

    assertDoesNotThrow(() -> useCase.execute(new ForgotPasswordCommand(unknownEmail)));
    verify(domainEventPublisher, never()).publishAll(any());
  }
}
