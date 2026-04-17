package com.ITJobsBackend.authentication.domain.valueobjects;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

public record PasswordResetToken(String value, Instant expiresAt) {

  private static final long EXPIRY_HOURS = 1;

  public PasswordResetToken {
    if (value == null || value.isBlank()) {
      throw new ValidationException("Reset token cannot be blank");
    }
    if (expiresAt == null) {
      throw new ValidationException("Reset token expiration cannot be null");
    }
  }

  public static PasswordResetToken generate() {
    return new PasswordResetToken(
        UUID.randomUUID().toString(), Instant.now().plus(EXPIRY_HOURS, ChronoUnit.HOURS));
  }

  public static PasswordResetToken of(String value, Instant expiresAt) {
    return new PasswordResetToken(value, expiresAt);
  }

  public boolean matches(String provided) {
    return value.equals(provided);
  }

  public boolean isExpired() {
    return Instant.now().isAfter(expiresAt);
  }

  @Override
  public String toString() {
    return "PasswordResetToken{value='[PROTECTED]', expiresAt=" + expiresAt + "}";
  }
}
