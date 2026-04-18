package com.ITJobsBackend.shared.domain.valueobjects;

import java.time.Instant;
import java.util.Objects;

/**
 * Value object wrapping an {@link Instant} to represent a point-in-time within the domain.
 *
 * <p>Used for {@code createdAt} and {@code updatedAt} fields across all aggregates.
 * Factory methods:
 * <ul>
 *   <li>{@link #now()} — current UTC instant</li>
 *   <li>{@link #of(Instant)} — wrap an existing instant (used by persistence mappers)</li>
 * </ul>
 */
public record Timestamp(Instant value) {

    public Timestamp {
        Objects.requireNonNull(value, "Timestamp cannot be null");
    }

    /** @return a {@code Timestamp} set to the current UTC instant */
    public static Timestamp now() {
        return new Timestamp(Instant.now());
    }

    /**
     * Wraps an existing {@link Instant}, typically when reconstituting from persistence.
     *
     * @param instant the instant to wrap; must not be {@code null}
     * @return a {@code Timestamp} wrapping the given instant
     */
    public static Timestamp of(Instant instant) {
        return new Timestamp(instant);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
