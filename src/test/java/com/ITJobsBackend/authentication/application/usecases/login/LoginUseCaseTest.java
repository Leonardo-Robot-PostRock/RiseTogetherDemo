package com.ITJobsBackend.authentication.application.usecases.login;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

import com.ITJobsBackend.shared.domain.valueobjects.Email;

import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.application.ports.out.PasswordEncoderPort;
import com.ITJobsBackend.authentication.application.ports.out.TokenGeneratorPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.exceptions.InvalidCredentialsException;
import com.ITJobsBackend.authentication.domain.service.CredentialsVerifier;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private TokenGeneratorPort tokenGenerator;

    private CredentialsVerifier credentialsVerifier;

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        credentialsVerifier = spy(new CredentialsVerifier());
        loginUseCase = new LoginUseCase(loadUserPort, passwordEncoder, tokenGenerator, credentialsVerifier);
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginCommand command = new LoginCommand("john@example.com", "password123");

        UserAggregate user = UserAggregate.create(
            Username.of("johndoe"),
            Email.of("john@example.com"),
            HashedPassword.fromHash("$2a$10$hashed")
        );

        when(loadUserPort.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "$2a$10$hashed")).thenReturn(true);
        when(tokenGenerator.generateAccessToken(any(), any())).thenReturn("access-token");
        when(tokenGenerator.generateRefreshToken(any())).thenReturn("refresh-token");

        AuthTokenResponse response = loginUseCase.execute(command);

        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        verify(credentialsVerifier).verifyCredentials(eq(user), eq("password123"), eq(passwordEncoder));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        LoginCommand command = new LoginCommand("nobody@example.com", "password123");

        when(loadUserPort.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(command));
        verifyNoInteractions(credentialsVerifier, tokenGenerator);
    }

    @Test
    void shouldPassRawPasswordWithoutValidation() {
        LoginCommand command = new LoginCommand("john@example.com", "short");

        UserAggregate user = UserAggregate.create(
            Username.of("johndoe"),
            Email.of("john@example.com"),
            HashedPassword.fromHash("$2a$10$hashed")
        );

        when(loadUserPort.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("short", "$2a$10$hashed")).thenReturn(true);
        when(tokenGenerator.generateAccessToken(any(), any())).thenReturn("access-token");
        when(tokenGenerator.generateRefreshToken(any())).thenReturn("refresh-token");

        loginUseCase.execute(command);

        verify(credentialsVerifier).verifyCredentials(eq(user), eq("short"), eq(passwordEncoder));
    }
}
