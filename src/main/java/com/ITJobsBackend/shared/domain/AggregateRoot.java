package com.ITJobsBackend.shared.domain;

import java.util.ArrayList;
import java.util.List;

import com.ITJobsBackend.shared.domain.event.DomainEvent;

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
