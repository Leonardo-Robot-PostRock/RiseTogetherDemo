package com.ITJobsBackend.jobs.infrastructure.events;

import com.ITJobsBackend.jobs.domain.event.JobDeactivatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class JobDeactivatedEventListener {
  private static final Logger log = LoggerFactory.getLogger(JobDeactivatedEventListener.class);

  @EventListener
  public void on(JobDeactivatedEvent event) {
    log.info("Job deactivated: '{}'", event.getJobTitle());
  }
}
