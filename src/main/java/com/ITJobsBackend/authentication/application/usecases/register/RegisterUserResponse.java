package com.ITJobsBackend.authentication.application.usecases.register;

import java.time.Instant;

public record RegisterUserResponse(
    String userId, String username, String email, Instant createdAt) {}
