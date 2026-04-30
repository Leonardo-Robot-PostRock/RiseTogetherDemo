package com.risetogether.authentication.infrastructure.adapters.in.rest.dto;

public record AuthResponse(
    String userId, String username, String email, String accessToken, String refreshToken) {}
