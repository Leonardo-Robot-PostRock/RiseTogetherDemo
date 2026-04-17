package com.ITJobsBackend.authentication.domain.event;

import com.ITJobsBackend.shared.domain.event.DomainEvent;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

public class EmailVerifiedEvent extends DomainEvent {
  private final Email email;

  public EmailVerifiedEvent(UserId userId, Email email) {
    super(userId, "UserAggregate", "email.verified");
    this.email = email;
  }

  public Email getEmail() {
    return email;
  }

  @Override
  public String toString() {
    return "EmailVerifiedEvent{"
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
