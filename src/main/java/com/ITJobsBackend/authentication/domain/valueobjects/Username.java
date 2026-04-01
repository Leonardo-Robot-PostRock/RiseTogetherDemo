package com.ITJobsBackend.authentication.domain.valueobjects;

import java.util.Objects;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

public final class Username {
  private static final int MIN_LENGTH = 3;
  private static final int MAX_LENGTH = 50;

  private final String value;

  private Username(String value) {
    this.value = value;
  }

  public static Username of(String username) {
    if (username == null || username.isBlank()) {
      throw new ValidationException("Username cannot be empty");
    }

    String trimmed = username.trim();

    if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
      throw new ValidationException(
          "Username must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters");
    }

    return new Username(trimmed);
  }

  public String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Username)) return false;
    Username other = (Username) o;
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
