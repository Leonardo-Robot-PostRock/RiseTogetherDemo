package com.risetogether.authentication.infrastructure.adapters.in.rest.dto;

import java.time.Instant;

public record RegisterResponse(String userId, String username, String email, Instant createdAt) {}
