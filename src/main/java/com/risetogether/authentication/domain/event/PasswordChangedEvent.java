package com.risetogether.authentication.domain.event;

import com.risetogether.shared.domain.event.DomainEvent;
import com.risetogether.shared.domain.valueobjects.UserId;

public class PasswordChangedEvent extends DomainEvent {

  public PasswordChangedEvent(UserId userId) {
    super(userId, "UserAggregate", UserEventTypes.PASSWORD_CHANGED);
  }

  @Override
  public String toString() {
    return "PasswordChangedEvent{" + baseFields() + '}';
  }
}
