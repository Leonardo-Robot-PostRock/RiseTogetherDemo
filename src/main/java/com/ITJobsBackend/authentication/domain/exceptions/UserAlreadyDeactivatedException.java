package com.ITJobsBackend.authentication.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.DomainException;

/**
 * Thrown when {@link com.ITJobsBackend.authentication.domain.aggregate.UserAggregate#deactivate()}
 * is called on a user that is not in the {@code ACTIVE} state.
 *
 * <p>Mapped to HTTP {@code 409 Conflict} by the {@code GlobalExceptionHandler}.
 */
public class UserAlreadyDeactivatedException extends DomainException {

  public UserAlreadyDeactivatedException(String message) {
    super(message);
  }
}
