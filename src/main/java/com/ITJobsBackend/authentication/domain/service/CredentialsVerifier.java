package com.ITJobsBackend.authentication.domain.service;

import com.ITJobsBackend.authentication.application.ports.out.PasswordEncoderPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.exceptions.InvalidCredentialsException;
import com.ITJobsBackend.shared.domain.valueobjects.Password;

public class CredentialsVerifier {

  public void verifyCredentials(
      UserAggregate user, Password rawPassword, PasswordEncoderPort encoder) {
    if (!encoder.matches(rawPassword.value(), user.getPassword().value())) {
      throw new InvalidCredentialsException();
    }
  }
}
