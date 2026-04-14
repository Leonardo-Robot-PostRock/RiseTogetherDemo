package com.ITJobsBackend.authentication.application.usecases.register;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
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
    // Given
    given(saveUserPort.existsByEmail(any(Email.class))).willReturn(false);
    given(passwordEncoder.encode(any())).willReturn("$2a$10$hashed");
    given(saveUserPort.save(any())).willAnswer(i -> i.getArgument(0));

    // When
    RegisterUserResponse response =
        useCase.execute(new RegisterUserCommand(USERNAME, EMAIL, PASSWORD));

    // Then
    assertNotNull(response.userId());
    assertEquals(USERNAME, response.username());
    assertEquals(EMAIL, response.email());
    then(saveUserPort).should().save(any());
    then(domainEventPublisher).should().publishAll(any());
  }

  @Test
  void shouldThrowExceptionWhenEmailAlreadyExists() {
    // Given
    String existingEmail = "existing@example.com";
    given(saveUserPort.existsByEmail(any(Email.class))).willReturn(true);

    // When & Then
    assertThrows(
        UserAlreadyExistsException.class,
        () -> useCase.execute(new RegisterUserCommand(USERNAME, existingEmail, PASSWORD)));
    then(saveUserPort).should(never()).save(any());
  }
}
