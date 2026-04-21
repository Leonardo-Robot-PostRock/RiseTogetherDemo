package com.ITJobsBackend.shared.application.ports.out;

import java.util.List;

import com.ITJobsBackend.shared.domain.event.DomainEvent;

public interface DomainEventPublisher {
  /**
   * Publishes all given domain events.
   *
   * <p>Accepts any {@code List} whose elements are {@link DomainEvent} or any subtype (PECS:
   * producer extends). This allows callers to pass {@code List<UserRegisteredEvent>} or {@code
   * List<JobCreatedEvent>} directly without casting.
   *
   * @param events the events to publish; must not be {@code null}
   */
  void publishAll(List<? extends DomainEvent> events);
}
