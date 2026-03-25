package com.ITJobsBackend.authentication.infrastructure.events;

import com.ITJobsBackend.authentication.domain.event.UserRegisteredEvent;
import com.ITJobsBackend.shared.infrastructure.ApplicationLogger;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventListener {
    private static final Logger log = ApplicationLogger.forClass(UserRegisteredEventListener.class);

    @EventListener
    public void on(UserRegisteredEvent event) {
        log.info("New user registered with email: {}", event.getEmail());
    }
}
