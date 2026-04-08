package com.ITJobsBackend.jobs.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.NotFoundException;

public class JobNotFoundException extends NotFoundException {

  public JobNotFoundException(String jobId) {
    super("Job not found with id: " + jobId);
  }
}
