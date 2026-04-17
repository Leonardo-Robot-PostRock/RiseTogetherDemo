package com.ITJobsBackend.authentication.domain.valueobjects;

public record HashedPassword(String value) {

    public HashedPassword {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Hash cannot be empty");
        }
    }

    public static HashedPassword fromHash(String hash) {
        return new HashedPassword(hash);
    }

    @Override
    public String toString() {
        return "[HASHED]";
    }
}
