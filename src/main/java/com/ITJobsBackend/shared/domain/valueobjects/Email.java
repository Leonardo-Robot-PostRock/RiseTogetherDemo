package com.ITJobsBackend.shared.domain.valueobjects;

import java.util.regex.Pattern;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

public record Email(String value) {

  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  public Email {
    if (value == null || value.isBlank()) {
      throw new ValidationException("Email cannot be empty");
    }

    value = value.trim().toLowerCase();

    if (!EMAIL_PATTERN.matcher(value).matches()) {
      throw new ValidationException("Invalid email format: " + value);
    }
  }

  public static Email of(String email) {
    return new Email(email);
  }

  public String mask() {
    int atIndex = value.indexOf('@');

    if (atIndex <= 1) {
      return "***@" + value.substring(atIndex + 1);
    }

    return value.charAt(0) + "***" + value.substring(atIndex);
  }

  @Override
  public String toString() {
    return mask();
  }
}
