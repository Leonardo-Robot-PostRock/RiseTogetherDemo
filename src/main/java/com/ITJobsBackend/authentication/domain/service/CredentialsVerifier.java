package com.ITJobsBackend.authentication.domain.service;

import com.ITJobsBackend.authentication.application.ports.out.PasswordEncoderPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.exceptions.InvalidCredentialsException;

/**
 * Domain service that verifies a user's raw password against their stored hash.
 *
 * <p>This logic lives in a domain service (rather than in {@code UserAggregate}) because it
 * depends on the {@link PasswordEncoderPort} infrastructure port, which the aggregate itself
 * must not reference.
 *
 * <p>Throws {@link InvalidCredentialsException} if the password does not match — a domain
 * exception that translates to {@code 401 Unauthorized} in the HTTP layer.
 */
public class CredentialsVerifier {

  /**
   * Verifies that {@code rawPassword} matches the hash stored in {@code user}.
   *
   * @param user        the user whose credentials are being checked
   * @param rawPassword the plain-text password submitted by the caller
   * @param encoder     the password encoder used to compare plain and hashed values
   * @throws InvalidCredentialsException if the password does not match
   */
  public void verifyCredentials(
      UserAggregate user, String rawPassword, PasswordEncoderPort encoder) {
    if (!encoder.matches(rawPassword, user.getPassword().value())) {
      throw new InvalidCredentialsException();
    }
  }
}
