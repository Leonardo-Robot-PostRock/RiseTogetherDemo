package com.ITJobsBackend.jobs.infrastructure.events;

import com.ITJobsBackend.jobs.domain.event.JobDeactivatedEvent;
import com.ITJobsBackend.shared.infrastructure.ApplicationLogger;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class JobDeactivatedEventListener {
    private static final Logger log = ApplicationLogger.forClass(JobDeactivatedEventListener.class);

    @EventListener
    public void on(JobDeactivatedEvent event) {
        log.info("Job deactivated: '{}'", event.getJobTitle());
    }
}
