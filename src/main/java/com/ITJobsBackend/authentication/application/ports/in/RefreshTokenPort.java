package com.ITJobsBackend.authentication.application.ports.in;

import com.ITJobsBackend.authentication.application.usecases.login.AuthTokenResponse;

public interface RefreshTokenPort {
  AuthTokenResponse execute(String refreshToken);
}