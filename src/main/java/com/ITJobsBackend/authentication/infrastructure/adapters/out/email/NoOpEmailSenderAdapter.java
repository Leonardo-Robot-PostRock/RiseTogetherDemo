package com.ITJobsBackend.authentication.infrastructure.adapters.out.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Repository;

import com.ITJobsBackend.authentication.application.ports.out.EmailSenderPort;

@Repository
@ConditionalOnMissingBean(EmailSenderPort.class)
public class NoOpEmailSenderAdapter implements EmailSenderPort {

    private static final Logger log = LoggerFactory.getLogger(NoOpEmailSenderAdapter.class);

    @Override
    public void sendEmail(String to, String subject, String body) {
        log.warn(
                "Email sending is disabled (spring.mail.host not configured). "
                        + "Dropping email to: {} | subject: {}",
                to,
                subject);
    }
}

