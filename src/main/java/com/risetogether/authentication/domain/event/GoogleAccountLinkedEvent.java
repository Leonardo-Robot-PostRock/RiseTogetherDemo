package com.risetogether.authentication.domain.event;

import com.risetogether.authentication.domain.valueobjects.GoogleSub;
import com.risetogether.shared.domain.event.DomainEvent;
import com.risetogether.shared.domain.valueobjects.UserId;

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
