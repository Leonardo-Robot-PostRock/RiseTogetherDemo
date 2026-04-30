package com.risetogether.authentication.domain.valueobjects;

import com.risetogether.shared.domain.exceptions.ValidationException;

/**
 * Value object representing the Google OAuth 2.0 subject identifier ({@code sub} claim)
 * returned in the Google ID token.
 *
 * <p>The {@code sub} is a stable, unique identifier for a Google account and is used to link
 * or look up users who sign in via Google OAuth.
 *
 * <p>Invariants:
 * <ul>
 *   <li>Must not be blank</li>
 *   <li>Must not exceed {@value #MAX_LENGTH} characters</li>
 * </ul>
 *
 * <p>{@link #toString()} always returns {@code "[GOOGLE_SUB_PROTECTED]"} to avoid PII exposure.
 */
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

    /**
     * @param googleSub the raw {@code sub} claim from the Google ID token
     * @return a valid {@code GoogleSub} instance
     * @throws ValidationException if the value is blank or exceeds the max length
     */
    public static GoogleSub of(String googleSub) {
        return new GoogleSub(googleSub);
    }

    @Override
    public String toString() {
        return "[GOOGLE_SUB_PROTECTED]";
    }
}
