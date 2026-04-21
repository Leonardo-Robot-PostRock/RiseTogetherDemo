package com.ITJobsBackend.authentication.application.usecases.refresh;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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

    // ── Constants ─────────────────────────────────────────────────────────────
    private static final String USER_ID         = "550e8400-e29b-41d4-a716-446655440000";
    private static final String EMAIL           = "test@example.com";
    private static final String USERNAME        = "testuser";
    private static final String HASHED_PASSWORD = "hashed";
    private static final String REFRESH_TOKEN   = "refresh-token";

    // ── Mocks (puertos de salida) ──────────────────────────────────────────────
    @Mock private TokenGeneratorPort tokenGenerator;
    @Mock private LoadUserPort       loadUserPort;

    // ── Subject under test ────────────────────────────────────────────────────
    @InjectMocks private RefreshTokenUseCase refreshTokenUseCase;

    // ── Helpers ───────────────────────────────────────────────────────────────
    private UserAggregate buildUser() {
        return UserAggregate.reconstitute(
                UserId.of(USER_ID),
                Username.of(USERNAME),
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
    void shouldGenerateNewTokens() {
        // Given
        given(tokenGenerator.extractUserId(REFRESH_TOKEN)).willReturn(USER_ID);
        given(loadUserPort.findById(any())).willReturn(Optional.of(buildUser()));
        given(tokenGenerator.generateAccessToken(any(), any())).willReturn("new-access-token");
        given(tokenGenerator.generateRefreshToken(any())).willReturn("new-refresh-token");

        // When
        AuthTokenResponse response = refreshTokenUseCase.execute(REFRESH_TOKEN);

        // Then
        assertNotNull(response);
        assertEquals(USER_ID,  response.userId());
        assertEquals(USERNAME, response.username());
        assertEquals(EMAIL,    response.email());
        assertEquals("new-access-token",  response.accessToken());
        assertEquals("new-refresh-token", response.refreshToken());
        then(tokenGenerator).should().extractUserId(REFRESH_TOKEN);
        then(loadUserPort).should().findById(any());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        // Given
        given(tokenGenerator.extractUserId(REFRESH_TOKEN)).willReturn("invalid-user-id");

        // When & Then
        assertThrows(
                IllegalArgumentException.class,
                () -> refreshTokenUseCase.execute(REFRESH_TOKEN));
    }
}