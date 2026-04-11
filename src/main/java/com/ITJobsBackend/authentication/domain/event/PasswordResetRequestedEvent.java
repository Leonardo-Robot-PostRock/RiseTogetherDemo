package com.ITJobsBackend.authentication.domain.event;

import com.ITJobsBackend.shared.domain.event.DomainEvent;

public class PasswordResetRequestedEvent extends DomainEvent {
  private final String email;
  private final String resetToken;

  public PasswordResetRequestedEvent(String userId, String email, String resetToken) {
    super(userId, "UserAggregate", "user.password_reset_requested");
    this.email = email;
    this.resetToken = resetToken;
  }

  public String getEmail() {
    return email;
  }

  public String getResetToken() {
    return resetToken;
  }
}