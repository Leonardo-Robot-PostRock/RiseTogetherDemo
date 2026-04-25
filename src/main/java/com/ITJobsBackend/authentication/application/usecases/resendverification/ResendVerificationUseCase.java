package com.ITJobsBackend.authentication.application.usecases.resendverification;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ITJobsBackend.authentication.application.ports.in.ResendVerificationPort;
import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.valueobjects.VerificationToken;
import com.ITJobsBackend.authentication.infrastructure.config.AuthProperties;
import com.ITJobsBackend.shared.domain.valueobjects.Email;

@Service
@Transactional
public class ResendVerificationUseCase implements ResendVerificationPort {
    private static final Logger log = LoggerFactory.getLogger(ResendVerificationUseCase.class);

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final AuthProperties authProperties;

    public ResendVerificationUseCase(
            LoadUserPort loadUserPort,
            SaveUserPort saveUserPort,
            AuthProperties authProperties) {
        this.loadUserPort = loadUserPort;
        this.saveUserPort = saveUserPort;
        this.authProperties = authProperties;
    }

    @Override
    public void execute(ResendVerificationCommand command) {
        log.info("Processing resend verification request for email: {}", command.email());

        Email email = Email.of(command.email());
        UserAggregate user = loadUserPort.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + command.email()));

        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email already verified for user: " + user.getId());
        }

        Instant expiresAt = Instant.now().plusSeconds(authProperties.getVerificationTokenExpirationHours() * 3600L);
        VerificationToken verificationToken = VerificationToken.of(UUID.randomUUID().toString(), expiresAt);
        user.assignVerificationToken(verificationToken);
        saveUserPort.save(user);

        log.info("New verification token generated for user: {}", user.getId());
    }
}