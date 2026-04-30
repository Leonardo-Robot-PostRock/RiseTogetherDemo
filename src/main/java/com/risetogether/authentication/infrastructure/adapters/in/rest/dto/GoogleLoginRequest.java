package com.risetogether.authentication.infrastructure.adapters.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GoogleLoginRequest(
    @NotBlank(message = "Google sub is required") String googleSub,
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
    String name,
    @NotNull(message = "Terms acceptance is required") Boolean termsAccepted) {}
