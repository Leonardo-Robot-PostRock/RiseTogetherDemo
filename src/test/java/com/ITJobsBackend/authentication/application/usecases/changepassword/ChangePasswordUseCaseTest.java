package com.ITJobsBackend.authentication.application.usecases.changepassword;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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
import com.ITJobsBackend.shared.application.ports.out.DomainEventPublisher;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class ChangePasswordUseCaseTest {

  // ── Constants ──────────────────────────────────────────────────────────────
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String EMAIL = "john@example.com";
  private static final String HASHED_PASSWORD = "$2a$10$hashed";
  private static final String OLD_PASSWORD = "OldP@ss123";
  private static final String NEW_PASSWORD = "NewP@ss456";
  private static final String NEW_HASH = "$2a$10$newHash";

  // ── Mocks (puertos de salida) ──────────────────────────────────────────────
  @Mock private LoadUserPort loadUserPort;
  @Mock private SaveUserPort saveUserPort;
  @Mock private PasswordEncoderPort passwordEncoder;
  @Mock private DomainEventPublisher domainEventPublisher;

  // ── Domain service (instancia real) ───────────────────────────────────────
  @Spy private CredentialsVerifier credentialsVerifier;

  // ── Subject under test ────────────────────────────────────────────────────
  @InjectMocks private ChangePasswordUseCase useCase;

  // ── Shared command ────────────────────────────────────────────────────────
  private ChangePasswordCommand command;

  @BeforeEach
  void setUp() {
    command = new ChangePasswordCommand(USER_ID, OLD_PASSWORD, NEW_PASSWORD);
  }

  // ── Helpers ───────────────────────────────────────────────────────────────
  private UserAggregate buildUser() {
    return UserAggregate.reconstitute(
        UserId.of(USER_ID),
        Username.of("john"),
        Email.of(EMAIL),
        HashedPassword.fromHash(HASHED_PASSWORD),
        true,
        true,
        null,
        Timestamp.now(),
        Timestamp.now(),
        List.of("ROLE_USER"),
        null);
  }

  // ── Tests ─────────────────────────────────────────────────────────────────
  @Test
  void shouldChangePasswordSuccessfully() {
    // Given
    UserAggregate user = buildUser();
    given(loadUserPort.findById(UserId.of(USER_ID))).willReturn(Optional.of(user));
    given(passwordEncoder.matches(OLD_PASSWORD, HASHED_PASSWORD)).willReturn(true);
    given(passwordEncoder.encode(NEW_PASSWORD)).willReturn(NEW_HASH);
    given(saveUserPort.save(user)).willReturn(user);

    // When
    useCase.execute(command);

    // Then
    then(credentialsVerifier).should().verifyCredentials(HashedPassword.fromHash(HASHED_PASSWORD), OLD_PASSWORD, passwordEncoder);
    then(saveUserPort).should().save(user);
    then(domainEventPublisher).should().publishAll(any());
  }

  @Test
  void shouldThrowExceptionWhenUserNotFound() {
    // Given
    given(loadUserPort.findById(UserId.of(USER_ID))).willReturn(Optional.empty());

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
    then(saveUserPort).should(never()).save(any(UserAggregate.class));
  }

  @Test
  void shouldThrowExceptionWhenOldPasswordIsIncorrect() {
    // Given
    UserAggregate user = buildUser();
    given(loadUserPort.findById(UserId.of(USER_ID))).willReturn(Optional.of(user));
    given(passwordEncoder.matches(OLD_PASSWORD, HASHED_PASSWORD)).willReturn(false);

    // When & Then
    assertThrows(InvalidCredentialsException.class, () -> useCase.execute(command));
    then(saveUserPort).should(never()).save(any(UserAggregate.class));
  }
}
