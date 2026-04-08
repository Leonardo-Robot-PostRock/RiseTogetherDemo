package com.ITJobsBackend.authentication.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.DomainException;

public class UserAlreadyActivatedException extends DomainException {

  public UserAlreadyActivatedException(String message) {
    super(message);
  }
}
