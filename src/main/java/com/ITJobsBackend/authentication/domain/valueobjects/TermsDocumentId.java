package com.ITJobsBackend.authentication.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

import com.ITJobsBackend.shared.domain.valueobjects.Identifier;

public record TermsDocumentId(UUID value) implements Identifier {

    public TermsDocumentId {
        Objects.requireNonNull(value, "TermsDocumentId cannot be null");
    }

    public static TermsDocumentId generate() {
        return new TermsDocumentId(UUID.randomUUID());
    }

    public static TermsDocumentId of(UUID uuid) {
        return new TermsDocumentId(uuid);
    }

    public static TermsDocumentId of(String id) {
        try {
            return new TermsDocumentId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid TermsDocumentId format: " + id);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

