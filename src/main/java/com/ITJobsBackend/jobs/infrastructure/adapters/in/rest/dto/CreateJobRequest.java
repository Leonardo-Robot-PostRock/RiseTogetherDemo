package com.ITJobsBackend.jobs.infrastructure.adapters.in.rest.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateJobRequest(
    @NotBlank String title,
    String description,
    @NotBlank String company,
    @NotBlank String location,
    @NotNull BigDecimal salaryMin,
    @NotNull BigDecimal salaryMax,
    @NotBlank String currency,
    @NotBlank String employmentType,
    String workModality,
    String employerId) {}
