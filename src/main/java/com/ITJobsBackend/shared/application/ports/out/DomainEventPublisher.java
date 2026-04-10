package com.ITJobsBackend.shared.application.ports.out;

import java.util.List;

import com.ITJobsBackend.shared.domain.event.DomainEvent;

public interface DomainEventPublisher {
  void publishAll(List<DomainEvent> events);
}
