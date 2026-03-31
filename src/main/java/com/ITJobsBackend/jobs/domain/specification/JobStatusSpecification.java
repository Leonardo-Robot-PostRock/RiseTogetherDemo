package com.ITJobsBackend.jobs.domain.specification;

import com.ITJobsBackend.jobs.domain.aggregate.JobAggregate;
import com.ITJobsBackend.jobs.domain.valueobjects.JobStatus;

public class JobStatusSpecification implements JobSpecification {
  private final JobStatus status;

  public JobStatusSpecification(JobStatus status) {
    this.status = status;
  }

  @Override
  public boolean isSatisfiedBy(JobAggregate job) {
    return job.getStatus() == status;
  }
}
