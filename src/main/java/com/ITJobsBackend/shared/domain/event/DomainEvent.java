package com.ITJobsBackend.shared.domain.event;

import java.time.Instant;
import java.util.UUID;

import com.ITJobsBackend.shared.domain.valueobjects.Identifier;

public abstract class DomainEvent {
  private final String eventId;
  private final String aggregateId;
  private final String aggregateType;
  private final String eventType;
  private final Instant occurredOn;

  protected DomainEvent(Identifier aggregateId, String aggregateType, String eventName) {
    this.eventId = UUID.randomUUID().toString();
    this.aggregateId = aggregateId.toString();
    this.aggregateType = aggregateType;
    this.eventType = eventName;
    this.occurredOn = Instant.now();
  }

  public String getEventId() {
    return eventId;
  }

  public String getAggregateId() {
    return aggregateId;
  }

  public String getEventType() {
    return eventType;
  }

  public String getAggregateType() {
    return aggregateType;
  }

  public Instant getOccurredOn() {
    return occurredOn;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName()
        + "{"
        + "aggregateId='"
        + aggregateId
        + '\''
        + ", aggregateType='"
        + aggregateType
        + '\''
        + ", eventType='"
        + eventType
        + '\''
        + ", occurredOn="
        + occurredOn
        + '}';
  }
}
