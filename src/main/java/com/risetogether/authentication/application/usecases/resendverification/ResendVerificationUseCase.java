package com.risetogether.authentication.application.usecases.resendverification;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.risetogether.authentication.application.ports.in.ResendVerificationPort;
import com.risetogether.authentication.application.ports.out.LoadUserPort;
import com.risetogether.authentication.application.ports.out.SaveUserPort;
import com.risetogether.authentication.application.ports.out.VerificationTokenExpirationPort;
import com.risetogether.authentication.domain.aggregate.UserAggregate;
import com.risetogether.authentication.domain.valueobjects.VerificationToken;
import com.risetogether.shared.domain.valueobjects.Email;

@Service
@Transactional
public class ResendVerificationUseCase implements ResendVerificationPort {
    private static final Logger log = LoggerFactory.getLogger(ResendVerificationUseCase.class);

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final VerificationTokenExpirationPort verificationTokenExpirationPort;

    public ResendVerificationUseCase(
            LoadUserPort loadUserPort,
            SaveUserPort saveUserPort,
            VerificationTokenExpirationPort verificationTokenExpirationPort) {
        this.loadUserPort = loadUserPort;
        this.saveUserPort = saveUserPort;
        this.verificationTokenExpirationPort = verificationTokenExpirationPort;
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

        Instant expiresAt = Instant.now().plusSeconds(verificationTokenExpirationPort.getVerificationTokenExpirationHours() * 3600L);
        VerificationToken verificationToken = VerificationToken.of(UUID.randomUUID().toString(), expiresAt);
        user.assignVerificationToken(verificationToken);
        saveUserPort.save(user);

        log.info("New verification token generated for user: {}", user.getId());
    }
}