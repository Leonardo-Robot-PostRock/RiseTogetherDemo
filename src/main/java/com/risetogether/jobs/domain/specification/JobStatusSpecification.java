package com.risetogether.jobs.domain.specification;

import com.risetogether.jobs.domain.aggregate.JobAggregate;
import com.risetogether.jobs.domain.valueobjects.JobStatus;

/** {@link JobSpecification} that matches jobs with an exact {@link JobStatus}. */
public record JobStatusSpecification(JobStatus status) implements JobSpecification {
  /**
   * @param status the status a job must have to satisfy this specification
   */
  public JobStatusSpecification {}

  @Override
  public boolean isSatisfiedBy(JobAggregate job) {
    return job.getStatus() == status;
  }
}
