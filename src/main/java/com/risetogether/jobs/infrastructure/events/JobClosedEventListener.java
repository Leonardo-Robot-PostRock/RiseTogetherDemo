package com.risetogether.jobs.infrastructure.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.risetogether.jobs.domain.event.JobClosedEvent;

@Component
public class JobClosedEventListener {
  private static final Logger log = LoggerFactory.getLogger(JobClosedEventListener.class);

  @EventListener
  public void on(JobClosedEvent event) {
    log.info("Job closed: '{}' at company '{}'", event.getJobTitle(), event.getCompany());
  }
}
