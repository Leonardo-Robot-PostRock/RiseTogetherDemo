package com.ITJobsBackend.authentication.infrastructure.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.ITJobsBackend.authentication.domain.event.UserActivatedEvent;

@Component
public class UserActivatedEventListener {
  private static final Logger log = LoggerFactory.getLogger(UserActivatedEventListener.class);

  @EventListener
  public void on(UserActivatedEvent event) {
    log.info("User activated: {}", event.getEmail());
  }
}
