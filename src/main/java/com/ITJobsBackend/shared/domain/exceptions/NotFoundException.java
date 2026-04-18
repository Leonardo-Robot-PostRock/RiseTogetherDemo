package com.ITJobsBackend.shared.domain.exceptions;

/**
 * Thrown when a requested resource cannot be found.
 *
 * <p>Mapped to HTTP {@code 404 Not Found} by the {@code GlobalExceptionHandler}.
 */
public class NotFoundException extends DomainException {

  /** @param message description of the missing resource */
  public NotFoundException(String message) {
    super(message);
  }
}
