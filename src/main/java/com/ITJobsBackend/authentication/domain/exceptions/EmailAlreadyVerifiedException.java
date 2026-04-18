package com.ITJobsBackend.authentication.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.DomainException;

/**
 * Thrown when an operation that requires an unverified email is invoked on a user whose
 * email is already verified (e.g. trying to re-verify or re-assign a verification token).
 *
 * <p>Mapped to HTTP {@code 409 Conflict} by the {@code GlobalExceptionHandler}.
 */
public class EmailAlreadyVerifiedException extends DomainException {

  public EmailAlreadyVerifiedException(String message) {
    super(message);
  }

  public EmailAlreadyVerifiedException() {
    super("Email is already verified");
  }
}
