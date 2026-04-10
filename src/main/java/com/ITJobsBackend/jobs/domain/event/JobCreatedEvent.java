package com.ITJobsBackend.jobs.domain.event;

import com.ITJobsBackend.shared.domain.event.DomainEvent;

public class JobCreatedEvent extends DomainEvent {
  private final String title;
  private final String company;

  public JobCreatedEvent(String jobId, String title, String company) {
    super(jobId, "JobAggregate", "job.created");
    this.title = title;
    this.company = company;
  }

  public String getTitle() {
    return title;
  }

  public String getCompany() {
    return company;
  }
}
