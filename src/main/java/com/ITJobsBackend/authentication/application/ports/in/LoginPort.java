package com.ITJobsBackend.authentication.application.ports.in;

import com.ITJobsBackend.authentication.application.usecases.login.AuthTokenResponse;
import com.ITJobsBackend.authentication.application.usecases.login.LoginCommand;

public interface LoginPort {
  AuthTokenResponse execute(LoginCommand command);
}
