package com.ITJobsBackend.authentication.application.usecases.changepassword;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.application.ports.out.PasswordEncoderPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.exceptions.InvalidCredentialsException;
import com.ITJobsBackend.authentication.domain.service.CredentialsVerifier;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class ChangePasswordUseCaseTest {

  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String OLD_PASSWORD = "OldP@ss123";
  private static final String NEW_PASSWORD = "NewP@ss456";
  private static final String NEW_HASH = "$2a$10$newHash";
  private static final String EMAIL = "john@example.com";

  @Mock private LoadUserPort loadUserPort;
  @Mock private SaveUserPort saveUserPort;
  @Mock private PasswordEncoderPort passwordEncoder;
  @Spy private CredentialsVerifier credentialsVerifier;

  @InjectMocks private ChangePasswordUseCase useCase;

  private ChangePasswordCommand command;

  @BeforeEach
  void setUp() {
    command = new ChangePasswordCommand(USER_ID, OLD_PASSWORD, NEW_PASSWORD);
  }

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
        null,
        null);
  }

  @Test
  void shouldChangePasswordSuccessfully() {
    UserAggregate user = buildUser();
    given(loadUserPort.findById(UserId.of(USER_ID))).willReturn(Optional.of(user));
    given(passwordEncoder.matches(OLD_PASSWORD, "$2a$10$hashed")).willReturn(true);
    given(passwordEncoder.encode(NEW_PASSWORD)).willReturn(NEW_HASH);

    useCase.execute(command);

    then(credentialsVerifier).should().verifyCredentials(user, OLD_PASSWORD, passwordEncoder);
    then(saveUserPort).should().save(user);
  }

  @Test
  void shouldThrowExceptionWhenUserNotFound() {
    given(loadUserPort.findById(UserId.of(USER_ID))).willReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
    then(saveUserPort).should(never()).save(any(UserAggregate.class));
  }

  @Test
  void shouldThrowExceptionWhenOldPasswordIsIncorrect() {
    UserAggregate user = buildUser();
    given(loadUserPort.findById(UserId.of(USER_ID))).willReturn(Optional.of(user));
    given(passwordEncoder.matches(OLD_PASSWORD, "$2a$10$hashed")).willReturn(false);

    assertThrows(InvalidCredentialsException.class, () -> useCase.execute(command));
    then(saveUserPort).should(never()).save(any(UserAggregate.class));
  }
}
