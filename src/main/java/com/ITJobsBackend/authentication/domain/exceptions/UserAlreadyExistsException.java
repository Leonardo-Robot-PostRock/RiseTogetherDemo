package com.ITJobsBackend.authentication.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.DomainException;

public class UserAlreadyExistsException extends DomainException {
  private static final long serialVersionUID = 1L;

  public UserAlreadyExistsException(String email) {
    super("User with email " + email + " already exists");
  }
}
