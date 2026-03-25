package com.ITJobsBackend.authentication.infrastructure.events;

import com.ITJobsBackend.authentication.domain.event.UserActivatedEvent;
import com.ITJobsBackend.shared.infrastructure.ApplicationLogger;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserActivatedEventListener {
    private static final Logger log = ApplicationLogger.forClass(UserActivatedEventListener.class);

    @EventListener
    public void on(UserActivatedEvent event) {
        log.info("User activated: {}", event.getEmail());
    }
}
