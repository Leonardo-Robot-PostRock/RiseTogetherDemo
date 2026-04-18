package com.ITJobsBackend.jobs.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.NotFoundException;

/**
 * Thrown when a {@link com.ITJobsBackend.jobs.domain.aggregate.JobAggregate} with the given
 * id cannot be found in the repository.
 *
 * <p>Mapped to HTTP {@code 404 Not Found} by the {@code GlobalExceptionHandler}.
 */
public class JobNotFoundException extends NotFoundException {

  /**
   * @param jobId the id of the job that could not be found
   */
  public JobNotFoundException(String jobId) {
    super("Job not found with id: " + jobId);
  }
}
