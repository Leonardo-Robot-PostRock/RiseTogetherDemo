package com.ITJobsBackend.authentication.application.usecases.login;

public record LoginCommand(
    String email,
    String password
) {}
