package com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
    @NotBlank(message = "User ID is required") String userId,
    @NotBlank(message = "Old password is required") String oldPassword,
    @NotBlank(message = "New password is required") String newPassword) {}
