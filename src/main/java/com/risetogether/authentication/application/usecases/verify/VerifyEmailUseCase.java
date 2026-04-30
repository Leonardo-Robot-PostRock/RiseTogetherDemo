package com.risetogether.authentication.application.usecases.verify;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.risetogether.authentication.application.ports.in.VerifyEmailPort;
import com.risetogether.authentication.application.ports.out.LoadUserPort;
import com.risetogether.authentication.application.ports.out.SaveUserPort;
import com.risetogether.authentication.application.ports.out.VerificationTokenValidatorPort;
import com.risetogether.authentication.domain.aggregate.UserAggregate;
import com.risetogether.shared.application.ports.out.DomainEventPublisher;
import com.risetogether.shared.domain.valueobjects.UserId;

@Service
@Transactional
public class VerifyEmailUseCase implements VerifyEmailPort {
  private static final Logger log = LoggerFactory.getLogger(VerifyEmailUseCase.class);

  private final LoadUserPort loadUserPort;
  private final SaveUserPort saveUserPort;
  private final DomainEventPublisher domainEventPublisher;
  private final VerificationTokenValidatorPort verificationTokenValidatorPort;

  public VerifyEmailUseCase(
      LoadUserPort loadUserPort,
      SaveUserPort saveUserPort,
      DomainEventPublisher domainEventPublisher,
      VerificationTokenValidatorPort verificationTokenValidatorPort) {
    this.loadUserPort = loadUserPort;
    this.saveUserPort = saveUserPort;
    this.domainEventPublisher = domainEventPublisher;
    this.verificationTokenValidatorPort = verificationTokenValidatorPort;
  }

  @Override
  public void execute(VerifyEmailCommand command) {
    log.info("Verifying email for user: {}", command.userId());

    UserId userId = UserId.of(command.userId());
    Optional<UserAggregate> userOpt = loadUserPort.findById(userId);

    UserAggregate user =
        userOpt.orElseThrow(
            () -> new IllegalArgumentException("User not found: " + command.userId()));

    verificationTokenValidatorPort.validate(user.getVerificationToken(), command.token());
    user.verifyEmail();
    saveUserPort.save(user);
    domainEventPublisher.publishAll(user.pullDomainEvents());

    log.info("Email verified successfully for user: {}", userId);
  }
}
