package com.ITJobsBackend.authentication.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.DomainException;

/**
 * Thrown by {@code VerifyEmailUseCase} (or any consumer of
 * {@link com.ITJobsBackend.authentication.domain.valueobjects.VerificationToken}) when the
 * supplied token has passed its expiry instant.
 *
 * <p>Mapped to HTTP {@code 410 Gone} by the {@code GlobalExceptionHandler}.
 */
public class VerificationTokenExpiredException extends DomainException {

  public VerificationTokenExpiredException(String message) {
    super(message);
  }

  public VerificationTokenExpiredException() {
    super("Verification token has expired");
  }
}