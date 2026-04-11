package com.ITJobsBackend.authentication.domain.valueobjects;

import java.util.Objects;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

public final class GoogleSub {
    private static final int MAX_LENGTH = 255;

    private final String value;

    private GoogleSub(String value) {
        this.value = value;
    }

    public static GoogleSub of(String googleSub) {
        if (googleSub == null || googleSub.isBlank()) {
            throw new ValidationException("Google Sub cannot be empty");
        }

        String trimmed = googleSub.trim();

        if (trimmed.length() > MAX_LENGTH) {
            throw new ValidationException(
                    "Google Sub must not exceed " + MAX_LENGTH + " characters");
        }

        return new GoogleSub(trimmed);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GoogleSub other)) return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}

