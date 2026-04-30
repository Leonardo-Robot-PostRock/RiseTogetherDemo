package com.risetogether.jobs.domain.specification;

import com.risetogether.jobs.domain.aggregate.JobAggregate;

/** {@link JobSpecification} that matches jobs from a specific company (case-insensitive). */
public record CompanySpecification(String company) implements JobSpecification {
  /**
   * @param company the exact company name to match (case-insensitive)
   */
  public CompanySpecification {}

  @Override
  public boolean isSatisfiedBy(JobAggregate job) {
    return job.getCompany().equalsIgnoreCase(company);
  }
}
