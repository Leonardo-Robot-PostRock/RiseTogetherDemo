package com.ITJobsBackend.authentication.application.usecases.verify;

public record VerifyEmailCommand(String userId, String token) {}