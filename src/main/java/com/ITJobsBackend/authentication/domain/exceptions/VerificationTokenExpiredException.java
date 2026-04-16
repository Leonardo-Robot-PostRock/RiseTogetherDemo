package com.ITJobsBackend.authentication.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.DomainException;

public class VerificationTokenExpiredException extends DomainException {

  public VerificationTokenExpiredException(String message) {
    super(message);
  }

  public VerificationTokenExpiredException() {
    super("Verification token has expired");
  }
}