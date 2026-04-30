package com.risetogether.authentication.domain.event;

import com.risetogether.shared.domain.event.DomainEvent;
import com.risetogether.shared.domain.valueobjects.Email;
import com.risetogether.shared.domain.valueobjects.UserId;

public class UserDeactivatedEvent extends DomainEvent {
  private final Email email;

  public UserDeactivatedEvent(UserId userId, Email email) {
    super(userId, "UserAggregate", UserEventTypes.USER_DEACTIVATED);
    this.email = email;
  }

  public Email getEmail() {
    return email;
  }

  @Override
  public String toString() {
    return "UserDeactivatedEvent{" + baseFields() + ", email='" + email.mask() + "'}";
  }
}
