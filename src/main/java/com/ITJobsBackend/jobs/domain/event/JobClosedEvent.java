package com.ITJobsBackend.jobs.domain.event;

import com.ITJobsBackend.shared.domain.event.DomainEvent;

public class JobClosedEvent extends DomainEvent {
  private final String jobTitle;
  private final String company;

  public JobClosedEvent(String jobId, String jobTitle, String company) {
    super(jobId, "JobAggregate");
    this.jobTitle = jobTitle;
    this.company = company;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public String getCompany() {
    return company;
  }
}
