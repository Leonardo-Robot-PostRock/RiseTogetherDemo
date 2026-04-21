package com.ITJobsBackend.shared.infrastructure.events;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.ITJobsBackend.shared.application.ports.out.DomainEventPublisher;
import com.ITJobsBackend.shared.domain.event.DomainEvent;

@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {
  private final ApplicationEventPublisher springPublisher;

  public SpringDomainEventPublisher(ApplicationEventPublisher springPublisher) {
    this.springPublisher = springPublisher;
  }

  @Override
  public void publishAll(List<? extends DomainEvent> events) {
    events.forEach(springPublisher::publishEvent);
  }
}
