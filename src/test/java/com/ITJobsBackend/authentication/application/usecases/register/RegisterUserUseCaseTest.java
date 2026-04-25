package com.ITJobsBackend.authentication.application.usecases.register;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.application.ports.out.LoadTermsDocumentPort;
import com.ITJobsBackend.authentication.application.ports.out.PasswordEncoderPort;
import com.ITJobsBackend.authentication.application.ports.out.QueryUserPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveTermsAcceptancePort;
import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.domain.aggregate.TermsDocument;
import com.ITJobsBackend.authentication.domain.exceptions.UserAlreadyExistsException;
import com.ITJobsBackend.authentication.domain.valueobjects.TermsType;
import com.ITJobsBackend.shared.application.ports.out.DomainEventPublisher;
import com.ITJobsBackend.shared.domain.exceptions.ValidationException;
import com.ITJobsBackend.shared.domain.valueobjects.Email;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

  private static final String USERNAME = "johndoe";
  private static final String EMAIL = "john@example.com";
  private static final String PASSWORD = "SecureP@ss123";

  @Mock private SaveUserPort saveUserPort;
  @Mock private QueryUserPort queryUserPort;
  @Mock private PasswordEncoderPort passwordEncoder;
  @Mock private DomainEventPublisher domainEventPublisher;
  @Mock private LoadTermsDocumentPort loadTermsDocumentPort;
  @Mock private SaveTermsAcceptancePort saveTermsAcceptancePort;
  @InjectMocks private RegisterUserUseCase useCase;

  @Test
  void shouldRegisterNewUser() {
    // Given
    TermsDocument tosDoc = TermsDocument.create(TermsType.TERMS_OF_SERVICE, "1", "ToS content");
    given(queryUserPort.existsByEmail(any(Email.class))).willReturn(false);
    given(passwordEncoder.encode(any())).willReturn("$2a$10$hashed");
    given(saveUserPort.save(any())).willAnswer(i -> i.getArgument(0));
    given(loadTermsDocumentPort.findLatestByType(TermsType.TERMS_OF_SERVICE))
        .willReturn(Optional.of(tosDoc));

    // When
    RegisterUserResponse response =
        useCase.execute(new RegisterUserCommand(USERNAME, EMAIL, PASSWORD, true));

    // Then
    assertNotNull(response.userId());
    assertEquals(USERNAME, response.username());
    assertEquals(EMAIL, response.email());
    then(saveUserPort).should().save(any());
    then(saveTermsAcceptancePort).should().save(any());
    then(domainEventPublisher).should().publishAll(any());
  }

  @Test
  void shouldThrowExceptionWhenTermsNotAccepted() {
    // When & Then
    assertThrows(
        ValidationException.class,
        () -> useCase.execute(new RegisterUserCommand(USERNAME, EMAIL, PASSWORD, false)));
    then(saveUserPort).should(never()).save(any());
  }

  @Test
  void shouldThrowExceptionWhenEmailAlreadyExists() {
    // Given
    given(queryUserPort.existsByEmail(any(Email.class))).willReturn(true);

    // When & Then
    assertThrows(
        UserAlreadyExistsException.class,
        () -> useCase.execute(new RegisterUserCommand(USERNAME, EMAIL, PASSWORD, true)));
    then(saveUserPort).should(never()).save(any());
  }
}
