package com.ITJobsBackend.authentication.domain.event;

import com.ITJobsBackend.shared.domain.event.DomainEvent;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

public class PasswordChangedEvent extends DomainEvent {

  public PasswordChangedEvent(UserId userId) {
    super(userId, "UserAggregate", UserEventTypes.PASSWORD_CHANGED);
  }

  @Override
  public String toString() {
    return "PasswordChangedEvent{" + baseFields() + '}';
  }
}
