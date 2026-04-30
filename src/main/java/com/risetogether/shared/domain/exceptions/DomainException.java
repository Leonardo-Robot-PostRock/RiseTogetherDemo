package com.risetogether.shared.domain.exceptions;

/**
 * Base class for all domain exceptions in the system.
 *
 * <p>Domain exceptions represent invariant violations or business rule failures that originate
 * inside the domain layer. They are unchecked exceptions and should be mapped to appropriate
 * HTTP responses by the {@code GlobalExceptionHandler} in the infrastructure layer.
 *
 * <p>Prefer concrete subclasses ({@link ValidationException}, {@link NotFoundException}, or
 * bounded-context-specific exceptions) over throwing this class directly.
 */
public abstract class DomainException extends RuntimeException {

  /**
   * @param message a human-readable description of the domain rule that was violated
   */
  public DomainException(String message) {
    super(message);
  }
}
