package com.ITJobsBackend.authentication.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.DomainException;

public class EmailAlreadyVerifiedException extends DomainException {

  public EmailAlreadyVerifiedException(String message) {
    super(message);
  }

  public EmailAlreadyVerifiedException() {
    super("Email is already verified");
  }
}