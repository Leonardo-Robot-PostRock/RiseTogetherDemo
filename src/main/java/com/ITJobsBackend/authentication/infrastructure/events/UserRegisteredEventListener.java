package com.ITJobsBackend.authentication.infrastructure.events;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.application.ports.out.VerificationTokenExpirationPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.event.UserRegisteredEvent;
import com.ITJobsBackend.authentication.domain.valueobjects.VerificationToken;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@Component
public class UserRegisteredEventListener {
  private static final Logger log = LoggerFactory.getLogger(UserRegisteredEventListener.class);

  private final LoadUserPort loadUserPort;
  private final SaveUserPort saveUserPort;
  private final VerificationTokenExpirationPort verificationTokenExpirationPort;

  public UserRegisteredEventListener(
      LoadUserPort loadUserPort,
      SaveUserPort saveUserPort,
      VerificationTokenExpirationPort verificationTokenExpirationPort) {
    this.loadUserPort = loadUserPort;
    this.saveUserPort = saveUserPort;
    this.verificationTokenExpirationPort = verificationTokenExpirationPort;
  }

  @EventListener
  public void on(UserRegisteredEvent event) {
    log.info("Generating verification token for user: {}", event.getUserId());

    UserId userId = UserId.of(event.getUserId().value());
    UserAggregate user =
        loadUserPort.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

    Instant expiresAt = Instant.now().plusSeconds(verificationTokenExpirationPort.getVerificationTokenExpirationHours() * 3600L);
    VerificationToken verificationToken = VerificationToken.of(UUID.randomUUID().toString(), expiresAt);

    user.assignVerificationToken(verificationToken);
    saveUserPort.save(user);

    log.info("Verification token generated for user: {}", userId);
  }
}
