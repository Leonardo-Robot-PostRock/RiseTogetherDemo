package com.ITJobsBackend.authentication.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.DomainException;

public class UserAlreadyDeactivatedException extends DomainException {

  public UserAlreadyDeactivatedException(String message) {
    super(message);
  }
}
