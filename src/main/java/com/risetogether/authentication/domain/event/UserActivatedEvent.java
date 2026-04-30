package com.risetogether.authentication.domain.event;

import com.risetogether.shared.domain.event.DomainEvent;
import com.risetogether.shared.domain.valueobjects.Email;
import com.risetogether.shared.domain.valueobjects.UserId;

public class UserActivatedEvent extends DomainEvent {
  private final Email email;

  public UserActivatedEvent(UserId userId, Email email) {
    super(userId, "UserAggregate", UserEventTypes.USER_ACTIVATED);
    this.email = email;
  }

  public Email getEmail() {
    return email;
  }

  @Override
  public String toString() {
    return "UserActivatedEvent{" + baseFields() + ", email='" + email.mask() + "'}";
  }
}
