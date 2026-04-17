package com.ITJobsBackend.authentication.domain.event;

import com.ITJobsBackend.shared.domain.event.DomainEvent;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

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
