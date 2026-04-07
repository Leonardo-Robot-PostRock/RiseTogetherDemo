package com.ITJobsBackend.jobs.application.ports.in;

import com.ITJobsBackend.jobs.application.usecases.createjob.CreateJobCommand;
import com.ITJobsBackend.jobs.application.usecases.searchjobs.JobResponse;

public interface CreateJobPort {
  JobResponse execute(CreateJobCommand command);
}