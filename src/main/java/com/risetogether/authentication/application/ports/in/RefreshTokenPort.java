package com.risetogether.authentication.application.ports.in;

import com.risetogether.authentication.application.usecases.login.AuthTokenResponse;

public interface RefreshTokenPort {
  AuthTokenResponse execute(String refreshToken);
}