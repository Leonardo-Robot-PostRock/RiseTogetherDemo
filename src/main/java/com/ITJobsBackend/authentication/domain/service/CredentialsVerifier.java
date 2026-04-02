package com.ITJobsBackend.authentication.domain.service;

import com.ITJobsBackend.authentication.application.ports.out.PasswordEncoderPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.exceptions.InvalidCredentialsException;

public class CredentialsVerifier {

  public void verifyCredentials(
      UserAggregate user, String rawPassword, PasswordEncoderPort encoder) {
    if (!encoder.matches(rawPassword, user.getPassword().value())) {
      throw new InvalidCredentialsException();
    }
  }
}
