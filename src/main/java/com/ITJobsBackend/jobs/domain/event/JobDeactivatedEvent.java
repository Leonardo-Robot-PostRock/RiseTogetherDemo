package com.ITJobsBackend.jobs.domain.event;

import com.ITJobsBackend.jobs.domain.valueobjects.JobId;
import com.ITJobsBackend.shared.domain.event.DomainEvent;

public class JobDeactivatedEvent extends DomainEvent {
  private final String jobTitle;

  public JobDeactivatedEvent(JobId jobId, String jobTitle) {
    super(jobId, "JobAggregate", "job.deactivated");
    this.jobTitle = jobTitle;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  @Override
  public String toString() {
    return "JobDeactivatedEvent{"
        + "aggregateId='"
        + getAggregateId()
        + '\''
        + ", aggregateType='"
        + getAggregateType()
        + '\''
        + ", eventType='"
        + getEventType()
        + '\''
        + ", occurredOn="
        + getOccurredOn()
        + ", jobTitle='"
        + jobTitle
        + '\''
        + '}';
  }
}
