package com.ITJobsBackend.authentication.domain.valueobjects;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

public record GoogleSub(String value) {

    private static final int MAX_LENGTH = 255;

    public GoogleSub {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Google Sub cannot be empty");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new ValidationException("Google Sub must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public static GoogleSub of(String googleSub) {
        return new GoogleSub(googleSub);
    }

    @Override
    public String toString() {
        return "[GOOGLE_SUB_PROTECTED]";
    }
}
