package com.ITJobsBackend.authentication.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

import com.ITJobsBackend.shared.domain.valueobjects.Identifier;

public record TermsAcceptanceId(UUID value) implements Identifier {

  public TermsAcceptanceId {
    Objects.requireNonNull(value, "TermsAcceptanceId cannot be null");
  }

  public static TermsAcceptanceId generate() {
    return new TermsAcceptanceId(UUID.randomUUID());
  }

  public static TermsAcceptanceId of(UUID uuid) {
    return new TermsAcceptanceId(uuid);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
