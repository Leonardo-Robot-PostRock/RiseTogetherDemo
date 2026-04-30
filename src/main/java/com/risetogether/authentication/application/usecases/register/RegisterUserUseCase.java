package com.risetogether.authentication.application.usecases.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.risetogether.authentication.application.ports.in.RegisterUserPort;
import com.risetogether.authentication.application.ports.out.LoadTermsDocumentPort;
import com.risetogether.authentication.application.ports.out.PasswordEncoderPort;
import com.risetogether.authentication.application.ports.out.QueryUserPort;
import com.risetogether.authentication.application.ports.out.SaveTermsAcceptancePort;
import com.risetogether.authentication.application.ports.out.SaveUserPort;
import com.risetogether.authentication.domain.aggregate.UserAggregate;
import com.risetogether.authentication.domain.entity.TermsAcceptance;
import com.risetogether.authentication.domain.exceptions.TermsDocumentNotFoundException;
import com.risetogether.authentication.domain.exceptions.UserAlreadyExistsException;
import com.risetogether.authentication.domain.valueobjects.HashedPassword;
import com.risetogether.authentication.domain.valueobjects.TermsType;
import com.risetogether.authentication.domain.valueobjects.Username;
import com.risetogether.shared.application.ports.out.DomainEventPublisher;
import com.risetogether.shared.domain.exceptions.ValidationException;
import com.risetogether.shared.domain.valueobjects.Email;
import com.risetogether.shared.domain.valueobjects.Password;

@Service
@Transactional
public class RegisterUserUseCase implements RegisterUserPort {
  private static final Logger log = LoggerFactory.getLogger(RegisterUserUseCase.class);

  private final SaveUserPort saveUserPort;
  private final QueryUserPort queryUserPort;
  private final PasswordEncoderPort passwordEncoder;
  private final DomainEventPublisher domainEventPublisher;
  private final LoadTermsDocumentPort loadTermsDocumentPort;
  private final SaveTermsAcceptancePort saveTermsAcceptancePort;

  public RegisterUserUseCase(
      SaveUserPort saveUserPort,
      QueryUserPort queryUserPort,
      PasswordEncoderPort passwordEncoder,
      DomainEventPublisher domainEventPublisher,
      LoadTermsDocumentPort loadTermsDocumentPort,
      SaveTermsAcceptancePort saveTermsAcceptancePort) {
    this.saveUserPort = saveUserPort;
    this.queryUserPort = queryUserPort;
    this.passwordEncoder = passwordEncoder;
    this.domainEventPublisher = domainEventPublisher;
    this.loadTermsDocumentPort = loadTermsDocumentPort;
    this.saveTermsAcceptancePort = saveTermsAcceptancePort;
  }

  @Override
  public RegisterUserResponse execute(RegisterUserCommand command) {
    log.info("Registering new user with email: {}", command.email());

    if (!command.termsAccepted()) {
      throw new ValidationException("Terms and conditions must be accepted");
    }

    Email email = Email.of(command.email());
    Username username = Username.of(command.username());
    Password rawPassword = Password.of(command.password());

    if (queryUserPort.existsByEmail(email)) {
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
