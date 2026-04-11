package com.ITJobsBackend.shared.domain.valueobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

class PasswordTest {

  @Test
  void shouldCreateValidPassword() {
    Password password = Password.of("SecureP@ss123");
    assertEquals("SecureP@ss123", password.value());
  }

  @Test
  void shouldThrowOnShortPassword() {
    assertThrows(ValidationException.class, () -> Password.of("Ab1@"));
  }

  @Test
  void shouldThrowOnNullPassword() {
    assertThrows(ValidationException.class, () -> Password.of(null));
  }

  @Test
  void shouldThrowWithoutUpperCase() {
    assertThrows(ValidationException.class, () -> Password.of("securep@ss123"));
  }

  @Test
  void shouldThrowWithoutLowerCase() {
    assertThrows(ValidationException.class, () -> Password.of("SECUREP@SS123"));
  }

  @Test
  void shouldThrowWithoutDigit() {
    assertThrows(ValidationException.class, () -> Password.of("SecureP@ssword"));
  }

  @Test
  void shouldThrowWithoutSpecialChar() {
    assertThrows(ValidationException.class, () -> Password.of("SecurePass123"));
  }

  @Test
  void shouldMaskToString() {
    Password password = Password.of("SecureP@ss123");
    assertEquals("[PROTECTED]", password.toString());
  }
}
