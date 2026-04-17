package com.ITJobsBackend.shared.domain.valueobjects;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

public record Password(String value) {

  private static final int MIN_LENGTH = 8;
  private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

  public Password {
    validate(value);
  }

  public static Password of(String password) {
    return new Password(password);
  }

  private static void validate(String value) {
    if (value == null || value.length() < MIN_LENGTH) {
      throw new ValidationException("Password must be at least " + MIN_LENGTH + " characters");
    }

    if (!hasUpperCase(value)) {
      throw new ValidationException("Password must contain uppercase letter");
    }

    if (!hasLowerCase(value)) {
      throw new ValidationException("Password must contain lowercase letter");
    }

    if (!hasDigit(value)) {
      throw new ValidationException("Password must contain digit");
    }

    if (!hasSpecialChar(value)) {
      throw new ValidationException("Password must contain special character: " + SPECIAL_CHARS);
    }
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

  @Override
  public String toString() {
    return "Password[PROTECTED]";
  }
}
