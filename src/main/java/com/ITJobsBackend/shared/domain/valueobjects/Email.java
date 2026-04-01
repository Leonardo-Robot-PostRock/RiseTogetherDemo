package com.ITJobsBackend.shared.domain.valueobjects;

import java.util.Objects;
import java.util.regex.Pattern;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

public final class Email {

  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  private final String value;

  private Email(String value) {
    this.value = value;
  }

  public static Email of(String email) {
    if (email == null || email.isBlank()) {
      throw new ValidationException("Email cannot be empty");
    }

    String normalized = email.trim().toLowerCase();

    if (!EMAIL_PATTERN.matcher(normalized).matches()) {
      throw new ValidationException("Invalid email format: " + email);
    }

    return new Email(normalized);
  }

  public String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Email)) return false;
    Email other = (Email) o;
    return Objects.equals(value, other.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
