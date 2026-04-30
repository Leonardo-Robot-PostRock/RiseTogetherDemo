package com.risetogether.authentication.domain.exceptions;

import com.risetogether.shared.domain.exceptions.DomainException;

/**
 * Thrown by {@code RegisterUserUseCase} when a registration attempt is made with an email
 * address that is already associated with an existing account.
 *
 * <p>Mapped to HTTP {@code 409 Conflict} by the {@code GlobalExceptionHandler}.
 */
public class UserAlreadyExistsException extends DomainException {

  /**
   * @param email the duplicate email address that triggered the conflict
   */
  public UserAlreadyExistsException(String email) {
    super("User with email " + email + " already exists");
  }
}
