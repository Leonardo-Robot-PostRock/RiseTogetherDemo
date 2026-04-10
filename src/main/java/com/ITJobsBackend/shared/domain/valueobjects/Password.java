package com.ITJobsBackend.shared.domain.valueobjects;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;
import java.util.Objects;

public final class Password {
  private static final int MIN_LENGTH = 8;
  private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

  private final String value;

  private Password(String value) {
    this.value = value;
  }

  public static Password of(String password) {
    if (password == null || password.length() < MIN_LENGTH) {
      throw new ValidationException("Password must be at least " + MIN_LENGTH + " characters");
    }

    if (!hasUpperCase(password)) {
      throw new ValidationException("Password must contain uppercase letter");
    }

    if (!hasLowerCase(password)) {
      throw new ValidationException("Password must contain lowercase letter");
    }

    if (!hasDigit(password)) {
      throw new ValidationException("Password must contain digit");
    }

    if (!hasSpecialChar(password)) {
      throw new ValidationException("Password must contain special character: " + SPECIAL_CHARS);
    }

    return new Password(password);
  }

  private static boolean hasUpperCase(String str) {
    return str.chars().anyMatch(Character::isUpperCase);
  }

  private static boolean hasLowerCase(String str) {
    return str.chars().anyMatch(Character::isLowerCase);
  }

  private static boolean hasDigit(String str) {
    return str.chars().anyMatch(Character::isDigit);
  }

  private static boolean hasSpecialChar(String str) {
    return str.chars().anyMatch(ch -> SPECIAL_CHARS.indexOf(ch) >= 0);
  }

  public String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Password)) return false;
    Password other = (Password) o;
    return Objects.equals(value, other.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "[PROTECTED]";
  }
}
