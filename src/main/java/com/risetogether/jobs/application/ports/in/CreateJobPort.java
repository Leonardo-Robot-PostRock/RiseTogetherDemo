package com.risetogether.jobs.application.ports.in;

import com.risetogether.jobs.application.usecases.createjob.CreateJobCommand;
import com.risetogether.jobs.application.usecases.searchjobs.JobResponse;

public interface CreateJobPort {
  JobResponse execute(CreateJobCommand command);
}
