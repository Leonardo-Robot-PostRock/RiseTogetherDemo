package com.risetogether.authentication.domain.valueobjects;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.risetogether.shared.domain.exceptions.ValidationException;

class VerificationTokenTest {

  // ── Constants ─────────────────────────────────────────────────────────────
  private static final String TOKEN_VALUE = "abc123";

  // ── Helpers ───────────────────────────────────────────────────────────────
  private VerificationToken buildValidToken() {
    return VerificationToken.of(TOKEN_VALUE, Instant.now().plusSeconds(3600));
  }

  private VerificationToken buildExpiredToken() {
    return VerificationToken.of(TOKEN_VALUE, Instant.now().minusSeconds(1));
  }

  // ── Tests ─────────────────────────────────────────────────────────────────
  @Test
  void shouldCreateVerificationToken() {
    // Given
    Instant expiresAt = Instant.now().plusSeconds(3600);

    // When
    VerificationToken token = VerificationToken.of(TOKEN_VALUE, expiresAt);

    // Then
    assertEquals(TOKEN_VALUE, token.token());
    assertEquals(expiresAt, token.expiresAt());
  }

  @Test
  void shouldThrowWhenTokenIsBlank() {
    // When & Then
    assertThrows(
        ValidationException.class, () -> VerificationToken.of("", Instant.now().plusSeconds(3600)));
  }

  @Test
  void shouldThrowWhenExpiresAtIsNull() {
    // When & Then
    assertThrows(ValidationException.class, () -> VerificationToken.of(TOKEN_VALUE, null));
  }

  @Test
  void shouldMatchCorrectToken() {
    // Given
    VerificationToken token = buildValidToken();

    // When
    boolean matches = token.matches(TOKEN_VALUE);

    // Then
    assertTrue(matches);
  }

  @Test
  void shouldNotMatchIncorrectToken() {
    // Given
    VerificationToken token = buildValidToken();

    // When
    boolean matches = token.matches("wrong");

    // Then
    assertFalse(matches);
  }

  @Test
  void shouldDetectExpiredToken() {
    // Given
    VerificationToken token = buildExpiredToken();

    // When
    boolean expired = token.isExpired();

    // Then
    assertTrue(expired);
  }

  @Test
  void shouldDetectNotExpiredToken() {
    // Given
    VerificationToken token = buildValidToken();

    // When
    boolean expired = token.isExpired();

    // Then
    assertFalse(expired);
  }
}
