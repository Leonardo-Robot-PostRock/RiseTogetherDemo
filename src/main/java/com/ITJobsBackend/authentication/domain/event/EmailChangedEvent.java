package com.ITJobsBackend.authentication.domain.event;

import com.ITJobsBackend.shared.domain.event.DomainEvent;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

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
