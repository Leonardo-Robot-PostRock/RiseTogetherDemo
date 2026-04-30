package com.risetogether.authentication.application.usecases.register;

public record RegisterUserCommand(
    String username,
    String email,
    String password,
    boolean termsAccepted) {}
