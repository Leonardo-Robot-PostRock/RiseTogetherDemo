package com.ITJobsBackend.authentication.domain.service;

import com.ITJobsBackend.authentication.application.ports.out.PasswordEncoderPort;
import com.ITJobsBackend.authentication.domain.exceptions.InvalidCredentialsException;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;

/**
 * Domain service that verifies a raw password against a stored hash.
 *
 * <p>Accepts {@link HashedPassword} directly instead of a full {@code UserAggregate}, so it can be
 * called from both command flows (where the caller holds an aggregate) and query flows (where the
 * caller holds a {@code UserView} projection).
 *
 * <p>Throws {@link InvalidCredentialsException} if the password does not match — a domain exception
 * that translates to {@code 401 Unauthorized} in the HTTP layer.
 */
public class CredentialsVerifier {

  /**
   * Verifies that {@code rawPassword} matches {@code stored}.
   *
   * @param stored the hashed password from persistence
   * @param rawPassword the plain-text password submitted by the caller
   * @param encoder the password encoder used to compare plain and hashed values
   * @throws InvalidCredentialsException if the password does not match
   */
  public void verifyCredentials(
      HashedPassword stored, String rawPassword, PasswordEncoderPort encoder) {
    if (!encoder.matches(rawPassword, stored.value())) {
      throw new InvalidCredentialsException();
    }
  }
}
