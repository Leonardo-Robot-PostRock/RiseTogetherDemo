package com.ITJobsBackend.authentication.domain.event;

import com.ITJobsBackend.shared.domain.event.DomainEvent;

public class UserDeactivatedEvent extends DomainEvent {
    private final String email;

    public UserDeactivatedEvent(String userId, String email) {
        super(userId, "UserAggregate", "user.deactivated");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
