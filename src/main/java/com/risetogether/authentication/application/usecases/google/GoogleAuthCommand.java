package com.risetogether.authentication.application.usecases.google;

public record GoogleAuthCommand(
    String googleSub, String email, String name, boolean termsAccepted) {}
