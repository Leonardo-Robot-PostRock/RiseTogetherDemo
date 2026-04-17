package com.ITJobsBackend.authentication.domain.event;

import com.ITJobsBackend.authentication.domain.valueobjects.GoogleSub;
import com.ITJobsBackend.shared.domain.event.DomainEvent;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

public class GoogleAccountLinkedEvent extends DomainEvent {
  private final GoogleSub googleSub;

  public GoogleAccountLinkedEvent(UserId userId, GoogleSub googleSub) {
    super(userId, "UserAggregate", UserEventTypes.GOOGLE_ACCOUNT_LINKED);
    this.googleSub = googleSub;
  }

  public GoogleSub getGoogleSub() {
    return googleSub;
  }

  @Override
  public String toString() {
    return "GoogleAccountLinkedEvent{" + baseFields() + ", googleSub=[PROTECTED]}";
  }
}
