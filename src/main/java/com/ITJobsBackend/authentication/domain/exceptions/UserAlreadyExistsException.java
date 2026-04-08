package com.ITJobsBackend.authentication.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.DomainException;

public class UserAlreadyExistsException extends DomainException {

  public UserAlreadyExistsException(String email) {
    super("User with email " + email + " already exists");
  }
}
