package com.ITJobsBackend.authentication.application.usecases.login;

public record AuthTokenResponse(
    String userId, String username, String email, String accessToken, String refreshToken) {}
