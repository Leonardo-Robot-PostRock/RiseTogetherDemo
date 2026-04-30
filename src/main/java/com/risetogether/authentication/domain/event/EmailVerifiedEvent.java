package com.risetogether.authentication.domain.event;

import com.risetogether.shared.domain.event.DomainEvent;
import com.risetogether.shared.domain.valueobjects.Email;
import com.risetogether.shared.domain.valueobjects.UserId;

public class EmailVerifiedEvent extends DomainEvent {
  private final Email email;

  public EmailVerifiedEvent(UserId userId, Email email) {
    super(userId, "UserAggregate", UserEventTypes.EMAIL_VERIFIED);
    this.email = email;
  }

  public Email getEmail() {
    return email;
  }

  @Override
  public String toString() {
    return "EmailVerifiedEvent{" + baseFields() + ", email='" + email.mask() + "'}";
  }
}
