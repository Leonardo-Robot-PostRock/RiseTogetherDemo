package com.ITJobsBackend.authentication.domain.event;

import com.ITJobsBackend.shared.domain.event.DomainEvent;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

public class UserRegisteredEvent extends DomainEvent {
  private final Email email;

  public UserRegisteredEvent(UserId userId, Email email) {
    super(userId, "UserAggregate", UserEventTypes.USER_REGISTERED);
    this.email = email;
  }

  public Email getEmail() {
    return email;
  }

  @Override
  public String toString() {
    return "UserRegisteredEvent{" + baseFields() + ", email='" + email.mask() + "'}";
  }
}
