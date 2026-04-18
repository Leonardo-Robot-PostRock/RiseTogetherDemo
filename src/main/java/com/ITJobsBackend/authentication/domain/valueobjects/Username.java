package com.ITJobsBackend.authentication.domain.valueobjects;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

/**
 * Value object representing a user's chosen display name.
 *
 * <p>Invariants (validated at construction time):
 * <ul>
 *   <li>Must not be blank</li>
 *   <li>Length between {@value #MIN_LENGTH} and {@value #MAX_LENGTH} characters (after trimming)</li>
 * </ul>
 *
 * <p>Use the factory method {@link #of(String)} to create instances.
 */
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

  /**
   * @param username the raw display name
   * @return a valid {@code Username} instance
   * @throws ValidationException if the value is blank or outside the allowed length range
   */
  public static Username of(String username) {
    return new Username(username);
  }

  @Override
  public String toString() {
    return value;
  }
}
