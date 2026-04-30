package com.risetogether.shared.domain.valueobjects;

import java.util.regex.Pattern;

import com.risetogether.shared.domain.exceptions.ValidationException;

/**
 * Value object representing a valid email address.
 *
 * <p>The value is normalised (trimmed and lower-cased) at construction time.
 * Use the factory method {@link #of(String)} to create instances.
 *
 * <p>Invariants:
 * <ul>
 *   <li>Must not be blank</li>
 *   <li>Must match the pattern {@code localPart@domain.tld}</li>
 * </ul>
 *
 * <p>{@link #toString()} returns a masked representation to avoid leaking PII in logs.
 * Use {@link #value()} only when the full address is explicitly required.
 */
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

  /**
   * Factory method — validates, normalises and wraps the given string.
   *
   * @param email the raw email address
   * @return a valid {@code Email} instance
   * @throws ValidationException if the value is blank or has an invalid format
   */
  public static Email of(String email) {
    return new Email(email);
  }

  /**
   * Returns a partially-masked version of the address suitable for logging (e.g. {@code j***@example.com}).
   *
   * @return masked email string
   */
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
