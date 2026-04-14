package com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequest(
    @NotBlank(message = "User ID is required") String userId,
    @NotBlank(message = "Token is required") String token) {}