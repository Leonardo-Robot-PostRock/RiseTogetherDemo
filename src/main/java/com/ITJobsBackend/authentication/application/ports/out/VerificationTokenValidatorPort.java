package com.ITJobsBackend.authentication.application.ports.out;

import com.ITJobsBackend.authentication.domain.valueobjects.VerificationToken;

public interface VerificationTokenValidatorPort {
    void validate(VerificationToken token, String providedToken);
}

