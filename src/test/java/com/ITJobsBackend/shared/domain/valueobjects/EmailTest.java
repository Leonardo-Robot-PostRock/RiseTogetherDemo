package com.ITJobsBackend.shared.domain.valueobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

class EmailTest {

  @Test
  void shouldCreateValidEmail() {
    // When
    Email email = Email.of("test@example.com");

    // Then
    assertEquals("test@example.com", email.value());
  }

  @Test
  void shouldNormalizeToLowerCase() {
    // When
    Email email = Email.of("Test@EXAMPLE.com");

    // Then
    assertEquals("test@example.com", email.value());
  }

  @Test
  void shouldThrowOnEmptyEmail() {
    // When & Then
    assertThrows(ValidationException.class, () -> Email.of(""));
  }

  @Test
  void shouldThrowOnNullEmail() {
    // When & Then
    assertThrows(ValidationException.class, () -> Email.of(null));
  }

  @Test
  void shouldThrowOnInvalidFormat() {
    // When & Then
    assertThrows(ValidationException.class, () -> Email.of("invalid-email"));
  }

  @Test
  void shouldBeEqualForSameValue() {
    // Given
    Email email1 = Email.of("test@example.com");
    Email email2 = Email.of("test@example.com");

    // Then
    assertEquals(email1, email2);
    assertEquals(email1.hashCode(), email2.hashCode());
  }
}
