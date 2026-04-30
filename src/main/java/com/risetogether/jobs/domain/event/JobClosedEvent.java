package com.risetogether.jobs.domain.event;

import com.risetogether.jobs.domain.valueobjects.JobId;
import com.risetogether.shared.domain.event.DomainEvent;

public class JobClosedEvent extends DomainEvent {
  private final String jobTitle;
  private final String company;

  public JobClosedEvent(JobId jobId, String jobTitle, String company) {
    super(jobId, "JobAggregate", JobEventTypes.JOB_CLOSED);
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
    return "JobClosedEvent{" + baseFields() + ", jobTitle='" + jobTitle + "', company='" + company + "'}";
  }
}
