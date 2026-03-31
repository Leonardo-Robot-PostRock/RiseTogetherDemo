package com.ITJobsBackend.authentication.domain.event;

import com.ITJobsBackend.shared.domain.event.DomainEvent;

public class UserRegisteredEvent extends DomainEvent {
  private final String email;

  public UserRegisteredEvent(String userId, String email) {
    super(userId, "UserAggregate");
    this.email = email;
  }

  public String getEmail() {
    return email;
  }
}
