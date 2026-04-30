package com.risetogether.jobs.domain.event;

import com.risetogether.jobs.domain.valueobjects.JobId;
import com.risetogether.shared.domain.event.DomainEvent;

public class JobCreatedEvent extends DomainEvent {
  private final String title;
  private final String company;

  public JobCreatedEvent(JobId jobId, String title, String company) {
    super(jobId, "JobAggregate", JobEventTypes.JOB_CREATED);
    this.title = title;
    this.company = company;
  }

  public String getTitle() {
    return title;
  }

  public String getCompany() {
    return company;
  }

  @Override
  public String toString() {
    return "JobCreatedEvent{" + baseFields() + ", title='" + title + "', company='" + company + "'}";
  }
}
