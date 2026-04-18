package com.ITJobsBackend.authentication.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

import com.ITJobsBackend.shared.domain.valueobjects.Identifier;

/**
 * Value object representing the unique identity of a
 * {@link com.ITJobsBackend.authentication.domain.aggregate.TermsDocument}.
 *
 * <p>Backed by a {@link UUID} and stored as {@code CHAR(36)} in the database.
 */
public record TermsDocumentId(UUID value) implements Identifier {

  public TermsDocumentId {
    Objects.requireNonNull(value, "TermsDocumentId cannot be null");
  }

  /** @return a new randomly-generated {@code TermsDocumentId} */
  public static TermsDocumentId generate() {
    return new TermsDocumentId(UUID.randomUUID());
  }

  /**
   * @param uuid existing UUID
   * @return wrapped {@code TermsDocumentId}
   */
  public static TermsDocumentId of(UUID uuid) {
    return new TermsDocumentId(uuid);
  }

  /**
   * @param id UUID string representation
   * @return parsed {@code TermsDocumentId}
   * @throws IllegalArgumentException if the string is not a valid UUID
   */
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
