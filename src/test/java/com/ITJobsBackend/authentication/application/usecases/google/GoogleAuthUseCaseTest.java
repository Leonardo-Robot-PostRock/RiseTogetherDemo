package com.ITJobsBackend.authentication.application.usecases.google;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

import com.ITJobsBackend.authentication.application.ports.out.*;
import com.ITJobsBackend.authentication.application.usecases.login.*;
import com.ITJobsBackend.authentication.domain.aggregate.*;
import com.ITJobsBackend.authentication.domain.exceptions.*;
import com.ITJobsBackend.authentication.domain.valueobjects.*;
import com.ITJobsBackend.shared.domain.valueobjects.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

  private UserAggregate buildUser() {
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
    given(tokenGenerator.generateAccessToken(any(), any())).willReturn("access-token");
    given(tokenGenerator.generateRefreshToken(any())).willReturn("refresh-token");
  }

  @Test
  void shouldAuthenticateNewGoogleUser() {
    given(loadUserPort.findByEmail(Email.of(EMAIL))).willReturn(Optional.empty());
    given(saveUserPort.save(any(UserAggregate.class)))
        .willAnswer(invocation -> invocation.getArgument(0));
    givenTokensAreStubbed();

    AuthTokenResponse response = useCase.execute(command);

    assertNotNull(response.userId());
    then(saveUserPort).should().save(any(UserAggregate.class));
  }

  @Test
  void shouldLoginExistingGoogleUser() {
    given(loadUserPort.findByEmail(Email.of(EMAIL))).willReturn(Optional.of(buildUser()));
    givenTokensAreStubbed();

    AuthTokenResponse response = useCase.execute(command);

    assertEquals(USER_ID, response.userId());
    then(saveUserPort).should(never()).save(any(UserAggregate.class));
  }

  @Test
  void shouldThrowExceptionWhenGoogleSubMismatch() {
    GoogleAuthCommand differentSubCommand =
        new GoogleAuthCommand("google-sub-DIFFERENT", EMAIL, NAME);
    given(loadUserPort.findByEmail(Email.of(EMAIL))).willReturn(Optional.of(buildUser()));

    assertThrows(InvalidCredentialsException.class, () -> useCase.execute(differentSubCommand));
    then(saveUserPort).should(never()).save(any(UserAggregate.class));
  }
}
