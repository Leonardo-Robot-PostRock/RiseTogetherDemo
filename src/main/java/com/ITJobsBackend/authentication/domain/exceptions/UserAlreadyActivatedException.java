package com.ITJobsBackend.authentication.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.DomainException;

/**
 * Thrown when {@link com.ITJobsBackend.authentication.domain.aggregate.UserAggregate#activate()}
 * is called on a user that is already in the {@code ACTIVE} state.
 *
 * <p>Mapped to HTTP {@code 409 Conflict} by the {@code GlobalExceptionHandler}.
 */
public class UserAlreadyActivatedException extends DomainException {

  public UserAlreadyActivatedException(String message) {
    super(message);
  }
}
