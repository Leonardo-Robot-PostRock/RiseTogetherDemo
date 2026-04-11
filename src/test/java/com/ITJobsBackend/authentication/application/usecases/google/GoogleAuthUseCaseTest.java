package com.ITJobsBackend.authentication.application.usecases.google;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.application.ports.out.TokenGeneratorPort;
import com.ITJobsBackend.authentication.application.usecases.login.AuthTokenResponse;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.valueobjects.GoogleSub;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class GoogleAuthUseCaseTest {

  @Mock private SaveUserPort saveUserPort;

  @Mock private LoadUserPort loadUserPort;

  @Mock private TokenGeneratorPort tokenGenerator;

  @InjectMocks private GoogleAuthUseCase useCase;

  @Test
  void shouldAuthenticateNewGoogleUser() {
    GoogleAuthCommand command =
        new GoogleAuthCommand("google-sub-123", "john@gmail.com", "John Doe");

    when(loadUserPort.findByEmail(any(Email.class))).thenReturn(java.util.Optional.empty());
    when(saveUserPort.save(any(UserAggregate.class))).thenAnswer(i -> i.getArgument(0));
    when(tokenGenerator.generateAccessToken(any(), any())).thenReturn("access-token");
    when(tokenGenerator.generateRefreshToken(any())).thenReturn("refresh-token");

    AuthTokenResponse response = useCase.execute(command);

    assertNotNull(response.userId());
    assertEquals("john@gmail.com", response.email());
    verify(saveUserPort).save(any(UserAggregate.class));
  }

  @Test
  void shouldLoginExistingGoogleUser() {
    GoogleAuthCommand command =
        new GoogleAuthCommand("google-sub-123", "john@gmail.com", "John Doe");
    UserAggregate existingUser =
        UserAggregate.reconstitute(
            UserId.of("550e8400-e29b-41d4-a716-446655440000"),
            Username.of("john"),
            Email.of("john@gmail.com"),
            HashedPassword.fromHash("$2a$10$hashed"),
            true,
            true,
            GoogleSub.of("google-sub-123"),
            Timestamp.now(),
            Timestamp.now(),
            List.of("ROLE_USER"));

    when(loadUserPort.findByEmail(any(Email.class)))
        .thenReturn(java.util.Optional.of(existingUser));
    when(tokenGenerator.generateAccessToken(any(), any())).thenReturn("access-token");
    when(tokenGenerator.generateRefreshToken(any())).thenReturn("refresh-token");

    AuthTokenResponse response = useCase.execute(command);

    assertEquals("550e8400-e29b-41d4-a716-446655440000", response.userId());
    verify(saveUserPort, never()).save(any());
  }
}
