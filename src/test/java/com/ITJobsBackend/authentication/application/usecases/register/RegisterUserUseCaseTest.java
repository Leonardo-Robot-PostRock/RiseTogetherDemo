package com.ITJobsBackend.authentication.application.usecases.register;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.application.ports.out.PasswordEncoderPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.domain.exceptions.UserAlreadyExistsException;
import com.ITJobsBackend.shared.application.ports.out.DomainEventPublisher;
import com.ITJobsBackend.shared.domain.valueobjects.Email;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

  @Mock private SaveUserPort saveUserPort;

  @Mock private PasswordEncoderPort passwordEncoder;

  @Mock private DomainEventPublisher domainEventPublisher;

  @InjectMocks private RegisterUserUseCase useCase;

  @Test
  void shouldRegisterNewUser() {
    RegisterUserCommand command =
        new RegisterUserCommand("johndoe", "john@example.com", "SecureP@ss123");

    when(saveUserPort.existsByEmail(any(Email.class))).thenReturn(false);
    when(passwordEncoder.encode(any())).thenReturn("$2a$10$hashed");
    when(saveUserPort.save(any())).thenAnswer(i -> i.getArgument(0));

    RegisterUserResponse response = useCase.execute(command);

    assertNotNull(response.userId());
    assertEquals("johndoe", response.username());
    assertEquals("john@example.com", response.email());
    verify(saveUserPort).save(any());
  }

  @Test
  void shouldThrowExceptionWhenEmailAlreadyExists() {
    RegisterUserCommand command =
        new RegisterUserCommand("johndoe", "existing@example.com", "SecureP@ss123");

    when(saveUserPort.existsByEmail(any(Email.class))).thenReturn(true);

    assertThrows(UserAlreadyExistsException.class, () -> useCase.execute(command));
    verify(saveUserPort, never()).save(any());
  }
}
