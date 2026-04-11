package com.ITJobsBackend.authentication.application.ports.in;

import com.ITJobsBackend.authentication.application.usecases.google.GoogleAuthCommand;
import com.ITJobsBackend.authentication.application.usecases.login.AuthTokenResponse;

public interface GoogleAuthPort {
  AuthTokenResponse execute(GoogleAuthCommand command);
}