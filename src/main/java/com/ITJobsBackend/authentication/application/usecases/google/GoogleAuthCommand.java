package com.ITJobsBackend.authentication.application.usecases.google;

public record GoogleAuthCommand(String googleSub, String email, String name) {}