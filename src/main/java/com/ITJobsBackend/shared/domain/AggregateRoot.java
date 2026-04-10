package com.ITJobsBackend.shared.domain;

import com.ITJobsBackend.shared.domain.event.DomainEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot {

  private final List<DomainEvent> domainEvents = new ArrayList<>();

  protected void recordEvent(DomainEvent event) {
    this.domainEvents.add(event);
  }

  public List<DomainEvent> pullDomainEvents() {
    List<DomainEvent> events = new ArrayList<>(domainEvents);
    domainEvents.clear();
    return events;
  }
}
