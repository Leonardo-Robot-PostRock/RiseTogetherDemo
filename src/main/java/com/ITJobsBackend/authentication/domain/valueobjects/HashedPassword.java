package com.ITJobsBackend.authentication.domain.valueobjects;

import java.util.Objects;

public final class HashedPassword {
    private final String value;

    private HashedPassword(String value) {
        this.value = value;
    }

    public static HashedPassword fromHash(String hash) {
        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException("Hash cannot be empty");
        }
        return new HashedPassword(hash);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HashedPassword)) return false;
        HashedPassword that = (HashedPassword) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "[HASHED]";
    }
}
