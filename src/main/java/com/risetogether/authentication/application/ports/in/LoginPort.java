package com.risetogether.authentication.application.ports.in;

import com.risetogether.authentication.application.usecases.login.AuthTokenResponse;
import com.risetogether.authentication.application.usecases.login.LoginCommand;

public interface LoginPort {
  AuthTokenResponse execute(LoginCommand command);
}
