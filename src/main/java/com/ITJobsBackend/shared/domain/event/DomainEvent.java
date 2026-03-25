package com.ITJobsBackend.shared.domain.event;

import java.time.Instant;
import java.util.UUID;

public abstract class DomainEvent {
    private final String eventId;
    private final String aggregateId;
    private final String aggregateType;
    private final Instant occurredOn;

    protected DomainEvent(String aggregateId, String aggregateType) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.occurredOn = Instant.now();
    }

    public String getEventId() { return eventId; }
    public String getAggregateId() { return aggregateId; }
    public String getAggregateType() { return aggregateType; }
    public Instant getOccurredOn() { return occurredOn; }
}
