package com.ITJobsBackend.authentication.application.usecases.refresh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.application.ports.out.TokenGeneratorPort;
import com.ITJobsBackend.authentication.application.usecases.login.AuthTokenResponse;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

  @Mock private TokenGeneratorPort tokenGenerator;
  @Mock private LoadUserPort loadUserPort;

  @InjectMocks private RefreshTokenUseCase refreshTokenUseCase;

  private UserAggregate user;

  @BeforeEach
  void setUp() {
    UserId userId = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    user =
        UserAggregate.reconstitute(
            userId,
            Username.of("testuser"),
            Email.of("test@example.com"),
            HashedPassword.fromHash("hashed"),
            true,
            true,
            null,
            Timestamp.now(),
            Timestamp.now(),
            List.of("ROLE_USER"),
            null);
  }

  @Test
  void shouldGenerateNewTokens() {
    given(tokenGenerator.extractUserId("refresh-token")).willReturn("550e8400-e29b-41d4-a716-446655440000");
    given(loadUserPort.findById(any())).willReturn(Optional.of(user));
    given(tokenGenerator.generateAccessToken(any(), any())).willReturn("new-access-token");
    given(tokenGenerator.generateRefreshToken(any())).willReturn("new-refresh-token");

    AuthTokenResponse response = refreshTokenUseCase.execute("refresh-token");

    assertNotNull(response);
    assertEquals("550e8400-e29b-41d4-a716-446655440000", response.userId());
    assertEquals("testuser", response.username());
    assertEquals("test@example.com", response.email());
    assertEquals("new-access-token", response.accessToken());
    assertEquals("new-refresh-token", response.refreshToken());
  }

  @Test
  void shouldThrowWhenUserNotFound() {
    given(tokenGenerator.extractUserId("refresh-token")).willReturn("invalid-user-id");

    assertThrows(IllegalArgumentException.class, () -> refreshTokenUseCase.execute("refresh-token"));
  }
}