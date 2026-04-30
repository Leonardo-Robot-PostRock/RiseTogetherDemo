package com.risetogether.authentication.application.ports.out;

import com.risetogether.authentication.domain.valueobjects.VerificationToken;

public interface VerificationTokenValidatorPort {
    void validate(VerificationToken token, String providedToken);
}

