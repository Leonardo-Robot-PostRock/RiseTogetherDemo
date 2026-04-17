package com.ITJobsBackend.shared.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record EmployerId(UUID value) implements Identifier {

    public EmployerId {
        Objects.requireNonNull(value, "EmployerId cannot be null");
    }

    public static EmployerId generate() {
        return new EmployerId(UUID.randomUUID());
    }

    public static EmployerId of(String id) {
        try {
            return new EmployerId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid EmployerId format: " + id);
        }
    }

    public static EmployerId of(UUID uuid) {
        return new EmployerId(uuid);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}