package com.ITJobsBackend.authentication.application.usecases.forgot;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
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
        List.of("ROLE_USER"));
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
}