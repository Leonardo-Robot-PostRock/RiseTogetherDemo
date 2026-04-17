package com.ITJobsBackend.authentication.domain.valueobjects;

import java.time.Instant;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

public record VerificationToken(String token, Instant expiresAt) {

  public VerificationToken {
    if (token == null || token.isBlank()) {
      throw new ValidationException("Verification token cannot be blank");
    }
    if (expiresAt == null) {
      throw new ValidationException("Verification token expiration cannot be null");
    }
  }

  public static VerificationToken of(String token, Instant expiresAt) {
    return new VerificationToken(token, expiresAt);
  }

  public boolean matches(String providedToken) {
    return token.equals(providedToken);
  }

  public boolean isExpired() {
    return Instant.now().isAfter(expiresAt);
  }

  @Override
  public String toString() {
    return "VerificationToken{token='[PROTECTED]', expiresAt=" + expiresAt + "}";
  }
}
