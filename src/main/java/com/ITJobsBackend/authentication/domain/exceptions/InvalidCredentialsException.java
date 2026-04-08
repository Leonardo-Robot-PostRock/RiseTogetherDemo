package com.ITJobsBackend.authentication.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.DomainException;

public class InvalidCredentialsException extends DomainException {

  public InvalidCredentialsException() {
    super("Invalid username or password");
  }
}
