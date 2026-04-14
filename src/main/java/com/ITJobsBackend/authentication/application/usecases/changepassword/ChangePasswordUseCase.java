package com.ITJobsBackend.authentication.application.usecases.changepassword;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ITJobsBackend.authentication.application.ports.in.ChangePasswordPort;
import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.application.ports.out.PasswordEncoderPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.service.CredentialsVerifier;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

import java.util.Optional;

@Service
@Transactional
public class ChangePasswordUseCase implements ChangePasswordPort {
  private static final Logger log = LoggerFactory.getLogger(ChangePasswordUseCase.class);

  private final LoadUserPort loadUserPort;
  private final SaveUserPort saveUserPort;
  private final PasswordEncoderPort passwordEncoder;
  private final CredentialsVerifier credentialsVerifier;

  public ChangePasswordUseCase(
      LoadUserPort loadUserPort,
      SaveUserPort saveUserPort,
      PasswordEncoderPort passwordEncoder,
      CredentialsVerifier credentialsVerifier) {
    this.loadUserPort = loadUserPort;
    this.saveUserPort = saveUserPort;
    this.passwordEncoder = passwordEncoder;
    this.credentialsVerifier = credentialsVerifier;
  }

  @Override
  public void execute(ChangePasswordCommand command) {
    log.info("Changing password for user: {}", command.userId());

    UserId userId = UserId.of(command.userId());
    Optional<UserAggregate> userOpt = loadUserPort.findById(userId);

    UserAggregate user =
        userOpt.orElseThrow(() -> new IllegalArgumentException("User not found: " + command.userId()));

    credentialsVerifier.verifyCredentials(user, command.oldPassword(), passwordEncoder);

    String newHashedPassword = passwordEncoder.encode(command.newPassword());
    user.changePassword(HashedPassword.fromHash(newHashedPassword));
    saveUserPort.save(user);

    log.info("Password changed successfully for user: {}", userId);
  }
}