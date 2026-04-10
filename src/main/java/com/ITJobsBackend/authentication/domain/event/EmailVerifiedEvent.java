package com.ITJobsBackend.authentication.domain.event;

import com.ITJobsBackend.shared.domain.event.DomainEvent;

public class EmailVerifiedEvent extends DomainEvent {
  private final String email;

  public EmailVerifiedEvent(String userId, String email) {
    super(userId, "UserAggregate", "email.verified");
    this.email = email;
  }

  public String getEmail() {
    return email;
  }
}
