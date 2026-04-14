package com.ITJobsBackend.authentication.application.usecases.verify;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.exceptions.EmailAlreadyVerifiedException;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.application.ports.out.DomainEventPublisher;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class VerifyEmailUseCaseTest {

  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String EMAIL = "john@example.com";
  private static final String TOKEN = "verification-token";

  @Mock private LoadUserPort loadUserPort;
  @Mock private SaveUserPort saveUserPort;
  @Mock private DomainEventPublisher domainEventPublisher;

  @InjectMocks private VerifyEmailUseCase useCase;

  private VerifyEmailCommand command;

  @BeforeEach
  void setUp() {
    command = new VerifyEmailCommand(USER_ID, TOKEN);
  }

  private UserAggregate buildUser(boolean active, boolean emailVerified) {
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
        List.of("ROLE_USER"));
  }

  @Test
  void shouldVerifyEmailSuccessfully() {
    UserAggregate user = buildUser(false, false);
    given(loadUserPort.findById(UserId.of(USER_ID))).willReturn(Optional.of(user));
    willDoNothing().given(domainEventPublisher).publishAll(any());

    useCase.execute(command);

    assertTrue(user.isEmailVerified());
    then(saveUserPort).should().save(user);
    then(domainEventPublisher).should().publishAll(any());
  }

  @Test
  void shouldThrowExceptionWhenEmailAlreadyVerified() {
    UserAggregate user = buildUser(true, true);
    given(loadUserPort.findById(UserId.of(USER_ID))).willReturn(Optional.of(user));

    assertThrows(EmailAlreadyVerifiedException.class, () -> useCase.execute(command));
    then(saveUserPort).should(never()).save(any(UserAggregate.class));
    then(domainEventPublisher).should(never()).publishAll(any());
  }
}