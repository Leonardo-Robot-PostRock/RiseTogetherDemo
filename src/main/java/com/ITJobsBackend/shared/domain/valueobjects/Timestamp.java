package com.ITJobsBackend.shared.domain.valueobjects;

import java.time.Instant;
import java.util.Objects;

public record Timestamp(Instant value) {

    public Timestamp {
        Objects.requireNonNull(value, "Timestamp cannot be null");
    }

    public static Timestamp now() {
        return new Timestamp(Instant.now());
    }

    public static Timestamp of(Instant instant) {
        return new Timestamp(instant);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
