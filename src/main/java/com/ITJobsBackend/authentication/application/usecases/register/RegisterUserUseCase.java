package com.ITJobsBackend.authentication.application.usecases.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ITJobsBackend.authentication.application.ports.in.RegisterUserPort;
import com.ITJobsBackend.authentication.application.ports.out.LoadTermsDocumentPort;
import com.ITJobsBackend.authentication.application.ports.out.PasswordEncoderPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveTermsAcceptancePort;
import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.entity.TermsAcceptance;
import com.ITJobsBackend.authentication.domain.exceptions.TermsDocumentNotFoundException;
import com.ITJobsBackend.authentication.domain.exceptions.UserAlreadyExistsException;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.TermsType;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.application.ports.out.DomainEventPublisher;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Password;

@Service
@Transactional
public class RegisterUserUseCase implements RegisterUserPort {
  private static final Logger log = LoggerFactory.getLogger(RegisterUserUseCase.class);

  private final SaveUserPort saveUserPort;
  private final PasswordEncoderPort passwordEncoder;
  private final DomainEventPublisher domainEventPublisher;
  private final LoadTermsDocumentPort loadTermsDocumentPort;
  private final SaveTermsAcceptancePort saveTermsAcceptancePort;

  public RegisterUserUseCase(
      SaveUserPort saveUserPort,
      PasswordEncoderPort passwordEncoder,
      DomainEventPublisher domainEventPublisher,
      LoadTermsDocumentPort loadTermsDocumentPort,
      SaveTermsAcceptancePort saveTermsAcceptancePort) {
    this.saveUserPort = saveUserPort;
    this.passwordEncoder = passwordEncoder;
    this.domainEventPublisher = domainEventPublisher;
    this.loadTermsDocumentPort = loadTermsDocumentPort;
    this.saveTermsAcceptancePort = saveTermsAcceptancePort;
  }

  @Override
  public RegisterUserResponse execute(RegisterUserCommand command) {
    log.info("Registering new user with email: {}", command.email());

    if (!command.termsAccepted()) {
      throw new IllegalArgumentException("Terms and conditions must be accepted");
    }

    Email email = Email.of(command.email());
    Username username = Username.of(command.username());
    Password rawPassword = Password.of(command.password());

    if (saveUserPort.existsByEmail(email)) {
      throw new UserAlreadyExistsException(email.value());
    }

    String hashedValue = passwordEncoder.encode(rawPassword.value());
    HashedPassword hashedPassword = HashedPassword.fromHash(hashedValue);

    UserAggregate user = UserAggregate.create(username, email, hashedPassword);
    UserAggregate savedUser = saveUserPort.save(user);

    recordTermsAcceptance(savedUser, TermsType.TERMS_OF_SERVICE);

    domainEventPublisher.publishAll(savedUser.pullDomainEvents());
    log.info("User registered successfully with ID: {}", savedUser.getId());

    return new RegisterUserResponse(
        savedUser.getId().value().toString(),
        savedUser.getUsername().value(),
        savedUser.getEmail().value(),
        savedUser.getCreatedAt().value());
  }

  private void recordTermsAcceptance(UserAggregate user, TermsType termsType) {
    var termsDocument = loadTermsDocumentPort
        .findLatestByType(termsType)
        .orElseThrow(() -> new TermsDocumentNotFoundException(termsType.name()));
    saveTermsAcceptancePort.save(
        TermsAcceptance.create(user.getId(), termsDocument.getId()));
  }
}
