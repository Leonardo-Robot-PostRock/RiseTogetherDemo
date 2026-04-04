package com.ITJobsBackend.authentication.domain.event;

import com.ITJobsBackend.shared.domain.event.DomainEvent;

public class UserActivatedEvent extends DomainEvent {
  private final String email;

  public UserActivatedEvent(String userId, String email) {
    super(userId, "UserAggregate", "user.activated");
    this.email = email;
  }

  public String getEmail() {
    return email;
  }
}
