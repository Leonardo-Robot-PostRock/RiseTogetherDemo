package com.ITJobsBackend.authentication.application.usecases.google;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.application.ports.out.TokenGeneratorPort;
import com.ITJobsBackend.authentication.application.usecases.login.AuthTokenResponse;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.exceptions.InvalidCredentialsException;
import com.ITJobsBackend.authentication.domain.valueobjects.GoogleSub;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class GoogleAuthUseCaseTest {

  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String EMAIL = "john@gmail.com";
  private static final String GOOGLE_SUB = "google-sub-123";
  private static final String NAME = "John Doe";

  @Mock private SaveUserPort saveUserPort;

  @Mock private LoadUserPort loadUserPort;

  @Mock private TokenGeneratorPort tokenGenerator;

  @InjectMocks private GoogleAuthUseCase useCase;

  private GoogleAuthCommand command;

  @BeforeEach
  void setUp() {
    command = new GoogleAuthCommand(GOOGLE_SUB, EMAIL, NAME);
  }

  private UserAggregate existingUser() {
    return UserAggregate.reconstitute(
        UserId.of(USER_ID),
        Username.of("john"),
        Email.of(EMAIL),
        HashedPassword.fromHash("$2a$10$hashed"),
        true,
        true,
        GoogleSub.of(GOOGLE_SUB),
        Timestamp.now(),
        Timestamp.now(),
        List.of("ROLE_USER"));
  }

  private void givenTokensAreStubbed() {
    when(tokenGenerator.generateAccessToken(any(), any())).thenReturn("access-token");
    when(tokenGenerator.generateRefreshToken(any())).thenReturn("refresh-token");
  }

  @Test
  void shouldAuthenticateNewGoogleUser() {
    when(loadUserPort.findByEmail(any(Email.class))).thenReturn(Optional.empty());
    when(saveUserPort.save(any(UserAggregate.class))).thenAnswer(i -> i.getArgument(0));
    givenTokensAreStubbed();

    AuthTokenResponse response = useCase.execute(command);

    assertNotNull(response.userId());
    assertEquals(EMAIL, response.email());
    verify(saveUserPort).save(any(UserAggregate.class));
  }

  @Test
  void shouldLoginExistingGoogleUser() {
    when(loadUserPort.findByEmail(any(Email.class))).thenReturn(Optional.of(existingUser()));
    givenTokensAreStubbed();

    AuthTokenResponse response = useCase.execute(command);

    assertEquals(USER_ID, response.userId());
    verify(saveUserPort, never()).save(any());
  }

  @Test
  void shouldFailIfGoogleSubMisMatch() {
    GoogleAuthCommand commandWithDifferentSub =
        new GoogleAuthCommand("google-sub-DIFFERENT", EMAIL, NAME);

    when(loadUserPort.findByEmail(any(Email.class))).thenReturn(Optional.of(existingUser()));

    assertThrows(
        InvalidCredentialsException.class, () -> useCase.execute(commandWithDifferentSub));
  }
}
