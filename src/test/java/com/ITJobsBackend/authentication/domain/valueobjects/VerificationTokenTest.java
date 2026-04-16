package com.ITJobsBackend.authentication.domain.valueobjects;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

class VerificationTokenTest {

    @Test
    void shouldCreateVerificationToken() {
        Instant expiresAt = Instant.now().plusSeconds(3600);
        VerificationToken token = VerificationToken.of("abc123", expiresAt);

        assertEquals("abc123", token.token());
        assertEquals(expiresAt, token.expiresAt());
    }

    @Test
    void shouldThrowWhenTokenIsBlank() {
        assertThrows(ValidationException.class,
            () -> VerificationToken.of("", Instant.now().plusSeconds(3600)));
    }

    @Test
    void shouldThrowWhenExpiresAtIsNull() {
        assertThrows(ValidationException.class,
            () -> VerificationToken.of("abc123", null));
    }

    @Test
    void shouldMatchCorrectToken() {
        VerificationToken token = VerificationToken.of("abc123", Instant.now().plusSeconds(3600));
        assertTrue(token.matches("abc123"));
    }

    @Test
    void shouldNotMatchIncorrectToken() {
        VerificationToken token = VerificationToken.of("abc123", Instant.now().plusSeconds(3600));
        assertFalse(token.matches("wrong"));
    }

    @Test
    void shouldDetectExpiredToken() {
        VerificationToken token = VerificationToken.of("abc123", Instant.now().minusSeconds(1));
        assertTrue(token.isExpired());
    }

    @Test
    void shouldDetectNotExpiredToken() {
        VerificationToken token = VerificationToken.of("abc123", Instant.now().plusSeconds(3600));
        assertFalse(token.isExpired());
    }
}

