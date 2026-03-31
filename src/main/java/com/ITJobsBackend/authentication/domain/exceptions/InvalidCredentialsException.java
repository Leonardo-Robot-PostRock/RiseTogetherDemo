package com.ITJobsBackend.authentication.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.DomainException;

public class InvalidCredentialsException extends DomainException {
  private static final long serialVersionUID = 1L;

  public InvalidCredentialsException() {
    super("Invalid username or password");
  }
}
