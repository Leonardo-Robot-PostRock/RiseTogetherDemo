package com.risetogether.authentication.domain.event;

import com.risetogether.shared.domain.event.DomainEvent;
import com.risetogether.shared.domain.valueobjects.Email;
import com.risetogether.shared.domain.valueobjects.UserId;

public class EmailChangedEvent extends DomainEvent {
  private final Email newEmail;

  public EmailChangedEvent(UserId userId, Email newEmail) {
    super(userId, "UserAggregate", UserEventTypes.EMAIL_CHANGED);
    this.newEmail = newEmail;
  }

  public Email getNewEmail() {
    return newEmail;
  }

  @Override
  public String toString() {
    return "EmailChangedEvent{" + baseFields() + ", newEmail='" + newEmail.mask() + "'}";
  }
}
