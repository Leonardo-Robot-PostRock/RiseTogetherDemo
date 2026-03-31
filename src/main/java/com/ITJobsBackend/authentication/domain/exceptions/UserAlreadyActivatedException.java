package com.ITJobsBackend.authentication.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.DomainException;

public class UserAlreadyActivatedException extends DomainException {
  private static final long serialVersionUID = 1L;

  public UserAlreadyActivatedException(String message) {
    super(message);
  }
}
