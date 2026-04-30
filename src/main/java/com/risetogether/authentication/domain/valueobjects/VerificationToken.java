package com.risetogether.authentication.domain.valueobjects;

import java.time.Instant;

import com.risetogether.shared.domain.exceptions.ValidationException;

/**
 * Value object representing a one-time email verification token.
 *
 * <p>The token is assigned to a {@code UserAggregate} after registration (via
 * {@link com.risetogether.authentication.domain.aggregate.UserAggregate#assignVerificationToken})
 * and consumed by {@code VerifyEmailUseCase}. Once verified the token is no longer needed.
 *
 * <p>Invariants:
 * <ul>
 *   <li>Token string must not be blank</li>
 *   <li>Expiration instant must not be null</li>
 * </ul>
 */
public record VerificationToken(String token, Instant expiresAt) {

  public VerificationToken {
    if (token == null || token.isBlank()) {
      throw new ValidationException("Verification token cannot be blank");
    }
    if (expiresAt == null) {
      throw new ValidationException("Verification token expiration cannot be null");
    }
  }

  /**
   * Factory method used by persistence mappers and infrastructure adapters.
   *
   * @param token     the raw token string
   * @param expiresAt when this token expires
   * @return a {@code VerificationToken} instance
   */
  public static VerificationToken of(String token, Instant expiresAt) {
    return new VerificationToken(token, expiresAt);
  }

  /**
   * Checks whether the provided token string matches this token.
   *
   * @param providedToken the token string submitted by the user
   * @return {@code true} if the strings are equal
   */
  public boolean matches(String providedToken) {
    return token.equals(providedToken);
  }

  /**
   * @return {@code true} if the current UTC instant is past {@link #expiresAt()}
   */
  public boolean isExpired() {
    return Instant.now().isAfter(expiresAt);
  }

  @Override
  public String toString() {
    return "VerificationToken{token='[PROTECTED]', expiresAt=" + expiresAt + "}";
  }
}
