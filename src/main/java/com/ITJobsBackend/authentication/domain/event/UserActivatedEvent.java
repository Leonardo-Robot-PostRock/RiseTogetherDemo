package com.ITJobsBackend.authentication.domain.event;

import com.ITJobsBackend.shared.domain.event.DomainEvent;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

public class UserActivatedEvent extends DomainEvent {
  private final Email email;

  public UserActivatedEvent(UserId userId, Email email) {
    super(userId, "UserAggregate", "user.activated");
    this.email = email;
  }

  public Email getEmail() {
    return email;
  }

  @Override
  public String toString() {
    return "UserActivatedEvent{"
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
        + ", email='"
        + email.mask()
        + '\''
        + '}';
  }
}
