package com.ITJobsBackend.authentication.infrastructure.adapters.out.security;

import org.springframework.stereotype.Component;

import com.ITJobsBackend.authentication.application.ports.out.VerificationTokenValidatorPort;
import com.ITJobsBackend.authentication.domain.exceptions.VerificationTokenExpiredException;
import com.ITJobsBackend.authentication.domain.valueobjects.VerificationToken;

@Component
public class VerificationTokenValidatorAdapter implements VerificationTokenValidatorPort {

    @Override
    public void validate(VerificationToken token, String providedToken) {
        if (token == null) {
            throw new IllegalArgumentException("No verification token has been set");
        }
        if (!token.matches(providedToken)) {
            throw new IllegalArgumentException("Invalid verification token");
        }
        if (token.isExpired()) {
            throw new VerificationTokenExpiredException("Verification token has expired");
        }
    }
}

