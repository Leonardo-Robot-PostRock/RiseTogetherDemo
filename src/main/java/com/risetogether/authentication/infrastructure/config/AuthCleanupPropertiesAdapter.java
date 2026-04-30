package com.risetogether.authentication.infrastructure.config;

import org.springframework.stereotype.Component;

import com.risetogether.authentication.application.ports.out.VerificationTokenExpirationPort;

@Component
public class AuthCleanupPropertiesAdapter implements VerificationTokenExpirationPort {

    private final AuthProperties authProperties;

    public AuthCleanupPropertiesAdapter(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Override
    public int getVerificationTokenExpirationHours() {
        return authProperties.getVerificationTokenExpirationHours();
    }
}