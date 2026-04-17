package com.ITJobsBackend.shared.domain.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

class PasswordTest {

  @Test
  void shouldCreateValidPassword() {
    // When
    Password password = Password.of("SecureP@ss123");

    // Then
    assertEquals("SecureP@ss123", password.value());
  }

  @Test
  void shouldThrowOnShortPassword() {
    // When & Then
    assertThrows(ValidationException.class, () -> Password.of("Ab1@"));
  }

  @Test
  void shouldThrowOnNullPassword() {
    // When & Then
    assertThrows(ValidationException.class, () -> Password.of(null));
  }

  @Test
  void shouldThrowWithoutUpperCase() {
    // When & Then
    assertThrows(ValidationException.class, () -> Password.of("securep@ss123"));
  }

  @Test
  void shouldThrowWithoutLowerCase() {
    // When & Then
    assertThrows(ValidationException.class, () -> Password.of("SECUREP@SS123"));
  }

  @Test
  void shouldThrowWithoutDigit() {
    // When & Then
    assertThrows(ValidationException.class, () -> Password.of("SecureP@ssword"));
  }

  @Test
  void shouldThrowWithoutSpecialChar() {
    // When & Then
    assertThrows(ValidationException.class, () -> Password.of("SecurePass123"));
  }

  @Test
  void shouldMaskToString() {
    // Given
    Password password = Password.of("SecureP@ss123");

    // When & Then
    assertEquals("Password[PROTECTED]", password.toString());
  }
}
