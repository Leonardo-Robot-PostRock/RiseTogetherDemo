package com.ITJobsBackend.authentication.application.ports.in;

import com.ITJobsBackend.authentication.application.usecases.login.LoginCommand;
import com.ITJobsBackend.authentication.application.usecases.login.AuthTokenResponse;

public interface LoginPort {
    AuthTokenResponse execute(LoginCommand command);
}
