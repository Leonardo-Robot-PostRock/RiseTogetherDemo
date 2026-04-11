package com.ITJobsBackend.authentication.application.usecases.forgot;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.application.ports.out.DomainEventPublisher;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordUseCaseTest {

  private static final String TEST_USER_ID = "550e8400-e29b-41d4-a716-446655440000";

  @Mock private LoadUserPort loadUserPort;

  @Mock private SaveUserPort saveUserPort;

  @Mock private DomainEventPublisher domainEventPublisher;

  @InjectMocks private ForgotPasswordUseCase useCase;

  @Test
  void shouldGenerateResetTokenForExistingUser() {
    ForgotPasswordCommand command = new ForgotPasswordCommand("john@example.com");

    UserAggregate user =
        UserAggregate.reconstitute(
            UserId.of(TEST_USER_ID),
            Username.of("john"),
            Email.of("john@example.com"),
            HashedPassword.fromHash("$2a$10$hashed"),
            true,
            true,
            null,
            Timestamp.now(),
            Timestamp.now(),
            List.of("ROLE_USER"));

    when(loadUserPort.findByEmail(Email.of("john@example.com"))).thenReturn(Optional.of(user));
    doNothing().when(domainEventPublisher).publishAll(any());

    useCase.execute(command);

    verify(loadUserPort).findByEmail(Email.of("john@example.com"));
    verify(domainEventPublisher).publishAll(any());
  }

  @Test
  void shouldDoNothingWhenUserNotFound() {
    ForgotPasswordCommand command = new ForgotPasswordCommand("nonexistent@example.com");

    when(loadUserPort.findByEmail(Email.of("nonexistent@example.com")))
        .thenReturn(Optional.empty());

    assertDoesNotThrow(() -> useCase.execute(command));
    verify(domainEventPublisher, never()).publishAll(any());
  }
}
