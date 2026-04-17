package com.ITJobsBackend.jobs.application.usecases.createjob;

import java.math.BigDecimal;

public record CreateJobCommand(
    String title,
    String description,
    String company,
    String location,
    BigDecimal salaryMin,
    BigDecimal salaryMax,
    String currency,
    String employmentType,
    String employerId) {}
