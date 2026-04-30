package com.risetogether.authentication.domain.valueobjects;

import com.risetogether.shared.domain.exceptions.ValidationException;

/**
 * Value object representing a bcrypt-hashed password stored in the database.
 *
 * <p>Instances are created via {@link #fromHash(String)} — wraps an already-encoded hash (from
 * persistence or from the {@code PasswordEncoderPort} after encoding a plain-text {@link
 * com.risetogether.shared.domain.valueobjects.Password}).
 *
 * <p>{@link #toString()} always returns {@code "[HASHED]"} to prevent the hash from leaking into
 * logs or serialised responses.
 */
public record HashedPassword(String value) {

  public HashedPassword {
    if (value == null || value.isBlank()) {
      throw new ValidationException("Hash cannot be empty");
    }
  }

  /**
   * Wraps a pre-encoded password hash.
   *
   * @param hash the bcrypt (or equivalent) encoded string; must not be blank
   * @return a {@code HashedPassword} wrapping the hash
   */
  public static HashedPassword fromHash(String hash) {
    return new HashedPassword(hash);
  }

  @Override
  public String toString() {
    return "[HASHED]";
  }
}
