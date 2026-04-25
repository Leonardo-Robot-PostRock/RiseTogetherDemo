package com.ITJobsBackend.authentication.application.usecases.forgot;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ITJobsBackend.authentication.application.ports.in.ForgotPasswordPort;
import com.ITJobsBackend.authentication.application.ports.out.QueryUserPort;
import com.ITJobsBackend.authentication.application.query.UserView;
import com.ITJobsBackend.authentication.domain.event.PasswordResetRequestedEvent;
import com.ITJobsBackend.authentication.domain.valueobjects.PasswordResetToken;
import com.ITJobsBackend.shared.application.ports.out.DomainEventPublisher;
import com.ITJobsBackend.shared.domain.event.DomainEvent;
import com.ITJobsBackend.shared.domain.valueobjects.Email;

@Service
@Transactional(readOnly = true)
public class ForgotPasswordUseCase implements ForgotPasswordPort {
  private static final Logger log = LoggerFactory.getLogger(ForgotPasswordUseCase.class);

  private final QueryUserPort queryUserPort;
  private final DomainEventPublisher domainEventPublisher;

  public ForgotPasswordUseCase(
      QueryUserPort queryUserPort, DomainEventPublisher domainEventPublisher) {
    this.queryUserPort = queryUserPort;
    this.domainEventPublisher = domainEventPublisher;
  }

  @Override
  public void execute(ForgotPasswordCommand command) {
    log.info("Processing password reset request for email: {}", command.email());

    Email email = Email.of(command.email());
    Optional<UserView> userOpt = queryUserPort.findByEmail(email);

    if (userOpt.isEmpty()) {
      log.warn("User not found for email: {}", command.email());
      return;
    }

    UserView user = userOpt.get();
    PasswordResetToken passwordResetToken = PasswordResetToken.generate();

    DomainEvent event = new PasswordResetRequestedEvent(user.id(), email, passwordResetToken);
    domainEventPublisher.publishAll(List.of(event));

    log.info("Password reset token generated for user: {}", user.id());
  }
}
