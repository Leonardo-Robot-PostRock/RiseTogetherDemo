package com.ITJobsBackend.jobs.domain.specification;

import com.ITJobsBackend.jobs.domain.aggregate.JobAggregate;

/**
 * Specification pattern interface for filtering {@link JobAggregate} instances.
 *
 * <p>Implementations encapsulate a single filtering criterion and can be composed via
 * {@link #and(JobSpecification)} and {@link #or(JobSpecification)} to build complex queries
 * in a type-safe, testable way.
 *
 * <p>Example usage:
 * <pre>{@code
 * JobSpecification spec = new TitleContainsSpecification("java")
 *     .and(new JobStatusSpecification(JobStatus.OPEN));
 * List<JobAggregate> results = jobRepo.findAll(spec);
 * }</pre>
 */
@FunctionalInterface
public interface JobSpecification {

  /**
   * Evaluates whether the given job satisfies this specification.
   *
   * @param job the job to evaluate
   * @return {@code true} if the job matches this criterion
   */
  boolean isSatisfiedBy(JobAggregate job);

  /**
   * Returns a composed specification that is satisfied only when both {@code this} and
   * {@code other} are satisfied.
   *
   * @param other the second specification
   * @return a new AND-composed {@code JobSpecification}
   */
  default JobSpecification and(JobSpecification other) {
    return job -> this.isSatisfiedBy(job) && other.isSatisfiedBy(job);
  }

  /**
   * Returns a composed specification that is satisfied when either {@code this} or
   * {@code other} is satisfied.
   *
   * @param other the second specification
   * @return a new OR-composed {@code JobSpecification}
   */
  default JobSpecification or(JobSpecification other) {
    return job -> this.isSatisfiedBy(job) || other.isSatisfiedBy(job);
  }
}
