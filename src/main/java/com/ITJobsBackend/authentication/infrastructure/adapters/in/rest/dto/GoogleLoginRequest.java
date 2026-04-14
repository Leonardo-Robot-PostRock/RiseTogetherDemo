package com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
    @NotBlank(message = "Google sub is required") String googleSub,
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
    String name) {}