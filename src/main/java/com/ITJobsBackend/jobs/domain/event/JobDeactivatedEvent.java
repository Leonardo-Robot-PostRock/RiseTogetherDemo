package com.ITJobsBackend.jobs.domain.event;

import com.ITJobsBackend.jobs.domain.valueobjects.JobId;
import com.ITJobsBackend.shared.domain.event.DomainEvent;

public class JobDeactivatedEvent extends DomainEvent {
  private final String jobTitle;

  public JobDeactivatedEvent(JobId jobId, String jobTitle) {
    super(jobId, "JobAggregate", JobEventTypes.JOB_DEACTIVATED);
    this.jobTitle = jobTitle;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  @Override
  public String toString() {
    return "JobDeactivatedEvent{" + baseFields() + ", jobTitle='" + jobTitle + "'}";
  }
}
