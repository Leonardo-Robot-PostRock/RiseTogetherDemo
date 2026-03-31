package com.ITJobsBackend.authentication.infrastructure.events;

import com.ITJobsBackend.authentication.domain.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventListener {
  private static final Logger log = LoggerFactory.getLogger(UserRegisteredEventListener.class);

  @EventListener
  public void on(UserRegisteredEvent event) {
    log.info("New user registered with email: {}", event.getEmail());
  }
}
