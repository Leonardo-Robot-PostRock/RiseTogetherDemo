package com.ITJobsBackend.jobs.application.usecases.searchjobs;

import java.math.BigDecimal;
import java.time.Instant;

public record JobResponse(
    String id,
    String title,
    String description,
    String company,
    String location,
    BigDecimal salaryMin,
    BigDecimal salaryMax,
    String currency,
    String employmentType,
    String workModality,
    String status,
    Instant createdAt) {}
