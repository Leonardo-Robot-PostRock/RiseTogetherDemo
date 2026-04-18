package com.ITJobsBackend.authentication.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

import com.ITJobsBackend.shared.domain.valueobjects.Identifier;

/**
 * Value object representing the unique identity of a
 * {@link com.ITJobsBackend.authentication.domain.entity.TermsAcceptance} audit record.
 *
 * <p>Backed by a {@link UUID} and stored as {@code CHAR(36)} in the database.
 */
public record TermsAcceptanceId(UUID value) implements Identifier {

  public TermsAcceptanceId {
    Objects.requireNonNull(value, "TermsAcceptanceId cannot be null");
  }

  /** @return a new randomly-generated {@code TermsAcceptanceId} */
  public static TermsAcceptanceId generate() {
    return new TermsAcceptanceId(UUID.randomUUID());
  }

  /**
   * @param uuid existing UUID
   * @return wrapped {@code TermsAcceptanceId}
   */
  public static TermsAcceptanceId of(UUID uuid) {
    return new TermsAcceptanceId(uuid);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
