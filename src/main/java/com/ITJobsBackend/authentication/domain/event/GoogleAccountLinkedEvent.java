package com.ITJobsBackend.authentication.domain.event;

import com.ITJobsBackend.authentication.domain.valueobjects.GoogleSub;
import com.ITJobsBackend.shared.domain.event.DomainEvent;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

public class GoogleAccountLinkedEvent extends DomainEvent {
  private final GoogleSub googleSub;

  public GoogleAccountLinkedEvent(UserId userId, GoogleSub googleSub) {
    super(userId, "UserAggregate", "user.google_account_linked");
    this.googleSub = googleSub;
  }

  public GoogleSub getGoogleSub() {
    return googleSub;
  }

  @Override
  public String toString() {
    return "GoogleAccountLinkedEvent{"
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
        + ", googleSub=[PROTECTED]"
        + '}';
  }
}

