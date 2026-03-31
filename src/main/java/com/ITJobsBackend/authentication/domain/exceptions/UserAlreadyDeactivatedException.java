package com.ITJobsBackend.authentication.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.DomainException;

public class UserAlreadyDeactivatedException extends DomainException {
  private static final long serialVersionUID = 1L;

  public UserAlreadyDeactivatedException(String message) {
    super(message);
  }
}
