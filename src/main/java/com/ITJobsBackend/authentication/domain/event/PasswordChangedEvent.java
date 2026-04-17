package com.ITJobsBackend.authentication.domain.event;

import com.ITJobsBackend.shared.domain.event.DomainEvent;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

public class PasswordChangedEvent extends DomainEvent {

  public PasswordChangedEvent(UserId userId) {
    super(userId, "UserAggregate", "user.password_changed");
  }

  @Override
  public String toString() {
    return "PasswordChangedEvent{"
        + "aggregateId='"
        + getAggregateId()
        + '\''
        + ", aggregateType='"
        + getAggregateType()
        + '\''
        + ", eventType='"
        + getEventType()
        + '\''
        + ", occurredOn="
        + getOccurredOn()
        + '}';
  }
}
