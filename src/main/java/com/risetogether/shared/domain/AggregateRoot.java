package com.risetogether.shared.domain;

import java.util.ArrayList;
import java.util.List;

import com.risetogether.shared.domain.event.DomainEvent;

/**
 * Base class for all aggregate roots in the domain.
 *
 * <p>Provides a mechanism to collect {@link DomainEvent domain events} raised during a business
 * operation. Events are held in memory and must be retrieved — and published — via
 * {@link #pullDomainEvents()} after the aggregate is saved to the repository.
 *
 * <p>Subclasses should call {@link #recordEvent(DomainEvent)} inside their domain methods to
 * register events without coupling themselves to any messaging infrastructure.
 */
public abstract class AggregateRoot {

  private final List<DomainEvent> domainEvents = new ArrayList<>();

  /**
   * Records a domain event to be published after the aggregate is persisted.
   *
   * @param event the event to record; must not be {@code null}
   */
  protected void recordEvent(DomainEvent event) {
    this.domainEvents.add(event);
  }

  /**
   * Returns all recorded domain events and clears the internal list.
   *
   * <p>This method is typically called by the application layer (or an infrastructure adapter)
   * immediately after saving the aggregate, to hand off the events to a
   * {@link com.risetogether.shared.application.ports.out.DomainEventPublisher}.
   *
   * @return a snapshot of the events recorded since the last call to this method
   */
  public List<DomainEvent> pullDomainEvents() {
    List<DomainEvent> events = List.copyOf(domainEvents);
    domainEvents.clear();
    return events;
  }
}
