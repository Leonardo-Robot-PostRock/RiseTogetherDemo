package com.risetogether.authentication.application.ports.in;

import com.risetogether.authentication.application.usecases.google.GoogleAuthCommand;
import com.risetogether.authentication.application.usecases.login.AuthTokenResponse;

public interface GoogleAuthPort {
  AuthTokenResponse execute(GoogleAuthCommand command);
}
