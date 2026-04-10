package com.ITJobsBackend.shared.application.ports.out;

import com.ITJobsBackend.shared.domain.event.DomainEvent;
import java.util.List;

public interface DomainEventPublisher {
  void publishAll(List<DomainEvent> events);
}
