package com.ITJobsBackend.authentication.application.usecases.register;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.application.ports.out.PasswordEncoderPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.domain.exceptions.UserAlreadyExistsException;
import com.ITJobsBackend.shared.application.ports.out.DomainEventPublisher;
import com.ITJobsBackend.shared.domain.valueobjects.Email;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

  private static final String USERNAME = "johndoe";
  private static final String EMAIL = "john@example.com";
  private static final String PASSWORD = "SecureP@ss123";

  @Mock private SaveUserPort saveUserPort;
  @Mock private PasswordEncoderPort passwordEncoder;
  @Mock private DomainEventPublisher domainEventPublisher;
  @InjectMocks private RegisterUserUseCase useCase;

  @Test
  void shouldRegisterNewUser() {
    when(saveUserPort.existsByEmail(any(Email.class))).thenReturn(false);
    when(passwordEncoder.encode(any())).thenReturn("$2a$10$hashed");
    when(saveUserPort.save(any())).thenAnswer(i -> i.getArgument(0));

    RegisterUserResponse response = useCase.execute(new RegisterUserCommand(USERNAME, EMAIL, PASSWORD));

    assertNotNull(response.userId());
    assertEquals(USERNAME, response.username());
    assertEquals(EMAIL, response.email());
    verify(saveUserPort).save(any());
    verify(domainEventPublisher).publishAll(any());
  }

  @Test
  void shouldThrowExceptionWhenEmailAlreadyExists() {
    String existingEmail = "existing@example.com";
    when(saveUserPort.existsByEmail(any(Email.class))).thenReturn(true);

    assertThrows(
        UserAlreadyExistsException.class,
        () -> useCase.execute(new RegisterUserCommand(USERNAME, existingEmail, PASSWORD)));
    verify(saveUserPort, never()).save(any());
  }
}
