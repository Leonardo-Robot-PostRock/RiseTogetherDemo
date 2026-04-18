package com.ITJobsBackend.authentication.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.DomainException;

/**
 * Thrown by {@link com.ITJobsBackend.authentication.domain.service.CredentialsVerifier} when
 * the supplied plain-text password does not match the stored hash.
 *
 * <p>Mapped to HTTP {@code 401 Unauthorized} by the {@code GlobalExceptionHandler}.
 * The fixed message deliberately avoids revealing whether the email or password was wrong.
 */
public class InvalidCredentialsException extends DomainException {

  public InvalidCredentialsException() {
    super("Invalid username or password");
  }
}
