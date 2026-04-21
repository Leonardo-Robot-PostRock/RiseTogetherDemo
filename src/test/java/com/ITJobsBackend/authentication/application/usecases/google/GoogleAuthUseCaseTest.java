package com.ITJobsBackend.authentication.application.usecases.google;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.application.ports.out.LoadTermsDocumentPort;
import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveTermsAcceptancePort;
import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.application.ports.out.TokenGeneratorPort;
import com.ITJobsBackend.authentication.application.usecases.login.AuthTokenResponse;
import com.ITJobsBackend.authentication.domain.aggregate.TermsDocument;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.exceptions.InvalidCredentialsException;
import com.ITJobsBackend.authentication.domain.valueobjects.GoogleSub;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.TermsType;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class GoogleAuthUseCaseTest {

    // ── Constants ─────────────────────────────────────────────────────────────
    private static final String USER_ID         = "550e8400-e29b-41d4-a716-446655440000";
    private static final String EMAIL           = "john@gmail.com";
    private static final String GOOGLE_SUB      = "google-sub-123";
    private static final String NAME            = "John Doe";
    private static final String HASHED_PASSWORD = "$2a$10$hashed";

    // ── Mocks (puertos de salida) ──────────────────────────────────────────────
    @Mock private SaveUserPort             saveUserPort;
    @Mock private LoadUserPort             loadUserPort;
    @Mock private TokenGeneratorPort       tokenGenerator;
    @Mock private LoadTermsDocumentPort    loadTermsDocumentPort;
    @Mock private SaveTermsAcceptancePort  saveTermsAcceptancePort;

    // ── Subject under test ────────────────────────────────────────────────────
    @InjectMocks private GoogleAuthUseCase useCase;

    // ── Shared command ────────────────────────────────────────────────────────
    private GoogleAuthCommand command;

    @BeforeEach
    void setUp() {
        command = new GoogleAuthCommand(GOOGLE_SUB, EMAIL, NAME, true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private UserAggregate buildUser() {
        return UserAggregate.reconstitute(
                UserId.of(USER_ID),
                Username.of("john"),
                Email.of(EMAIL),
                HashedPassword.fromHash(HASHED_PASSWORD),
                true,
                true,
                GoogleSub.of(GOOGLE_SUB),
                Timestamp.now(),
                Timestamp.now(),
                List.of("ROLE_USER"),
                null);
    }

    private UserAggregate buildUserWithoutGoogleSub() {
        return UserAggregate.reconstitute(
                UserId.of(USER_ID),
                Username.of("john"),
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

    private void givenTokensAreStubbed() {
        given(tokenGenerator.generateAccessToken(any(), any())).willReturn("access-token");
        given(tokenGenerator.generateRefreshToken(any())).willReturn("refresh-token");
    }

    private void givenTermsDocumentExists() {
        given(loadTermsDocumentPort.findLatestByType(TermsType.TERMS_OF_SERVICE))
                .willReturn(Optional.of(TermsDocument.create(TermsType.TERMS_OF_SERVICE, "1", "ToS")));
    }

    // ── Tests ─────────────────────────────────────────────────────────────────
    @Test
    void shouldAuthenticateNewGoogleUser() {
        // Given
        given(loadUserPort.findByEmail(Email.of(EMAIL))).willReturn(Optional.empty());
        given(saveUserPort.save(any(UserAggregate.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        givenTokensAreStubbed();
        givenTermsDocumentExists();

        // When
        AuthTokenResponse response = useCase.execute(command);

        // Then
        assertNotNull(response.userId());
        then(saveUserPort).should().save(any(UserAggregate.class));
        then(saveTermsAcceptancePort).should().save(any());
    }

    @Test
    void shouldLoginExistingGoogleUser() {
        // Given
        given(loadUserPort.findByEmail(Email.of(EMAIL))).willReturn(Optional.of(buildUser()));
        givenTokensAreStubbed();

        // When
        AuthTokenResponse response = useCase.execute(command);

        // Then
        assertEquals(USER_ID, response.userId());
        then(saveUserPort).should(never()).save(any(UserAggregate.class));
        then(saveTermsAcceptancePort).should(never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenGoogleSubMismatch() {
        // Given
        GoogleAuthCommand differentSubCommand =
                new GoogleAuthCommand("google-sub-DIFFERENT", EMAIL, NAME, true);
        given(loadUserPort.findByEmail(Email.of(EMAIL))).willReturn(Optional.of(buildUser()));

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> useCase.execute(differentSubCommand));
        then(saveUserPort).should(never()).save(any(UserAggregate.class));
    }

    @Test
    void shouldAuthenticateExistingUserWithNullGoogleSub() {
        // Given
        given(loadUserPort.findByEmail(Email.of(EMAIL)))
                .willReturn(Optional.of(buildUserWithoutGoogleSub()));
        givenTokensAreStubbed();

        // When
        AuthTokenResponse response = useCase.execute(command);

        // Then
        assertEquals(USER_ID, response.userId());
        then(saveUserPort).should(never()).save(any(UserAggregate.class));
        then(saveTermsAcceptancePort).should(never()).save(any());
    }

    @Test
    void shouldDeriveUsernameFromEmailWhenNameIsNull() {
        // Given
        GoogleAuthCommand nullNameCommand = new GoogleAuthCommand(GOOGLE_SUB, EMAIL, null, true);
        given(loadUserPort.findByEmail(Email.of(EMAIL))).willReturn(Optional.empty());
        given(saveUserPort.save(any(UserAggregate.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        givenTokensAreStubbed();
        givenTermsDocumentExists();

        // When
        useCase.execute(nullNameCommand);

        // Then
        ArgumentCaptor<UserAggregate> captor = ArgumentCaptor.forClass(UserAggregate.class);
        then(saveUserPort).should().save(captor.capture());
        assertEquals("john", captor.getValue().getUsername().value());
    }
}
