package com.ITJobsBackend.authentication.domain.event;

import com.ITJobsBackend.authentication.domain.valueobjects.PasswordResetToken;
import com.ITJobsBackend.shared.domain.event.DomainEvent;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

public class PasswordResetRequestedEvent extends DomainEvent {
  private final Email email;
  private final PasswordResetToken passwordResetToken;

  public PasswordResetRequestedEvent(UserId userId, Email email, PasswordResetToken passwordResetToken) {
    super(userId, "UserAggregate", "user.password_reset_requested");
    this.email = email;
    this.passwordResetToken = passwordResetToken;
  }

  public Email getEmail() {
    return email;
  }

  public PasswordResetToken getResetToken() {
    return passwordResetToken;
  }

  @Override
  public String toString() {
    return "PasswordResetRequestedEvent{"
        + "aggregateId='"
        + getAggregateId()
        + '\''
        + ", aggregateType='"
        + getAggregateType()
        + '\''
        + ", eventType='"
        + getEventType()
        + '\''
        + ", occurredOn="
        + getOccurredOn()
        + ", email='"
        + email.mask()
        + '\''
        + ", resetToken=[PROTECTED]"
        + '}';
  }
}
