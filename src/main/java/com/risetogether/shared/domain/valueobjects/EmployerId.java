package com.risetogether.shared.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing the unique identity of an {@code EmployerAggregate}.
 *
 * <p>Backed by a {@link UUID} and stored as {@code CHAR(36)} in the database.
 * Use the factory methods:
 * <ul>
 *   <li>{@link #generate()} — new random identifier</li>
 *   <li>{@link #of(String)} — parse from UUID string</li>
 *   <li>{@link #of(UUID)} — wrap an existing UUID</li>
 * </ul>
 */
public record EmployerId(UUID value) implements Identifier {

    public EmployerId {
        Objects.requireNonNull(value, "EmployerId cannot be null");
    }

    /** @return a new randomly-generated {@code EmployerId} */
    public static EmployerId generate() {
        return new EmployerId(UUID.randomUUID());
    }

    /**
     * @param id UUID string representation
     * @return parsed {@code EmployerId}
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static EmployerId of(String id) {
        try {
            return new EmployerId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid EmployerId format: " + id);
        }
    }

    /**
     * @param uuid existing UUID
     * @return wrapped {@code EmployerId}
     */
    public static EmployerId of(UUID uuid) {
        return new EmployerId(uuid);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}