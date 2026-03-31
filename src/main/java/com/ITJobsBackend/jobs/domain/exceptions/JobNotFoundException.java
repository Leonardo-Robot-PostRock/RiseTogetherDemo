package com.ITJobsBackend.jobs.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.NotFoundException;

public class JobNotFoundException extends NotFoundException {
  private static final long serialVersionUID = 1L;

  public JobNotFoundException(String jobId) {
    super("Job not found with id: " + jobId);
  }
}
