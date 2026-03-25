package com.ITJobsBackend.jobs.domain.event;

import com.ITJobsBackend.shared.domain.event.DomainEvent;

public class JobDeactivatedEvent extends DomainEvent {
    private final String jobTitle;

    public JobDeactivatedEvent(String jobId, String jobTitle) {
        super(jobId, "JobAggregate");
        this.jobTitle = jobTitle;
    }

    public String getJobTitle() { return jobTitle; }
}
