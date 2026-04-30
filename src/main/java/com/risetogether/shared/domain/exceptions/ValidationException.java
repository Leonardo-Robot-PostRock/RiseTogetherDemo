package com.risetogether.shared.domain.exceptions;

/**
 * Thrown when a domain invariant or input validation rule is violated.
 *
 * <p>Typical usages include value-object construction failures (e.g. invalid email format,
 * blank required fields) and aggregate method preconditions.
 * Mapped to HTTP {@code 400 Bad Request} by the {@code GlobalExceptionHandler}.
 */
public class ValidationException extends DomainException {

  /** @param message human-readable description of the validation failure */
  public ValidationException(String message) {
    super(message);
  }
}
