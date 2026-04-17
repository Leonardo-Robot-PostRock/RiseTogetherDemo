package com.ITJobsBackend.authentication.domain.valueobjects;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

public record Username(String value) {

  private static final int MIN_LENGTH = 3;
  private static final int MAX_LENGTH = 50;

  public Username {
    if (value == null || value.isBlank()) {
      throw new ValidationException("Username cannot be empty");
    }
    value = value.trim();
    if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
      throw new ValidationException(
          "Username must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters");
    }
  }

  public static Username of(String username) {
    return new Username(username);
  }

  @Override
  public String toString() {
    return value;
  }
}
