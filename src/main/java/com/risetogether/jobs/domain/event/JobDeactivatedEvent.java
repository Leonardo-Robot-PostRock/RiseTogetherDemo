package com.risetogether.jobs.domain.event;

import com.risetogether.jobs.domain.valueobjects.JobId;
import com.risetogether.shared.domain.event.DomainEvent;

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
