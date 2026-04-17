package com.ITJobsBackend.jobs.domain.event;

import com.ITJobsBackend.jobs.domain.valueobjects.JobId;
import com.ITJobsBackend.shared.domain.event.DomainEvent;

public class JobClosedEvent extends DomainEvent {
  private final String jobTitle;
  private final String company;

  public JobClosedEvent(JobId jobId, String jobTitle, String company) {
    super(jobId, "JobAggregate", "job.closed");
    this.jobTitle = jobTitle;
    this.company = company;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public String getCompany() {
    return company;
  }

  @Override
  public String toString() {
    return "JobClosedEvent{"
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
        + ", company='"
        + company
        + '\''
        + '}';
  }
}
