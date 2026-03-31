package com.ITJobsBackend.jobs.domain.specification;

import com.ITJobsBackend.jobs.domain.aggregate.JobAggregate;

@FunctionalInterface
public interface JobSpecification {
  boolean isSatisfiedBy(JobAggregate job);

  default JobSpecification and(JobSpecification other) {
    return job -> this.isSatisfiedBy(job) && other.isSatisfiedBy(job);
  }

  default JobSpecification or(JobSpecification other) {
    return job -> this.isSatisfiedBy(job) || other.isSatisfiedBy(job);
  }
}
