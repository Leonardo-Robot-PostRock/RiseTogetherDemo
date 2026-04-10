package com.ITJobsBackend.authentication.infrastructure.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.ITJobsBackend.authentication.domain.event.UserDeactivatedEvent;

@Component
public class UserDeactivatedEventListener {
  private final Logger log = LoggerFactory.getLogger(UserDeactivatedEventListener.class);

  @EventListener
  public void on(UserDeactivatedEvent event) {
    log.info("User deactivated: {}", event.getEmail());
  }
}
