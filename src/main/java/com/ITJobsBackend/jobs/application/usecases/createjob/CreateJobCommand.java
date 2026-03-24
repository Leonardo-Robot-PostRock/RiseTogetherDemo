package com.ITJobsBackend.jobs.application.usecases.createjob;

public record CreateJobCommand(
    String title,
    String description,
    String company,
    String location,
    double salaryMin,
    double salaryMax,
    String currency,
    String employmentType
) {}
