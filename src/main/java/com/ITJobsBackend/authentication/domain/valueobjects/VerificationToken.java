package com.ITJobsBackend.authentication.domain.valueobjects;

import java.time.Instant;
import java.util.Objects;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

public class VerificationToken {
    private final String token;
    private final Instant expiresAt;

    private VerificationToken(String token, Instant expiresAt) {
        if (token == null || token.isBlank()) {
            throw new ValidationException("Verification token cannot be blank");
        }
        if (expiresAt == null) {
            throw new ValidationException("Verification token expiration cannot be null");
        }
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public static VerificationToken of(String token, Instant expiresAt) {
        return new VerificationToken(token, expiresAt);
    }

    public boolean matches(String providedToken) {
        return token.equals(providedToken);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public String token() { return token; }

    public Instant expiresAt() { return expiresAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationToken that = (VerificationToken) o;
        return Objects.equals(token, that.token) && Objects.equals(expiresAt, that.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, expiresAt);
    }
}

