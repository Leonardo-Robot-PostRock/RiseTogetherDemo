package com.ITJobsBackend.jobs.infrastructure.events;

import com.ITJobsBackend.jobs.domain.event.JobClosedEvent;
import com.ITJobsBackend.shared.infrastructure.ApplicationLogger;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class JobClosedEventListener {
    private static final Logger log = ApplicationLogger.forClass(JobClosedEventListener.class);

    @EventListener
    public void on(JobClosedEvent event) {
        log.info("Job closed: '{}' at company '{}'", event.getJobTitle(), event.getCompany());
    }
}
