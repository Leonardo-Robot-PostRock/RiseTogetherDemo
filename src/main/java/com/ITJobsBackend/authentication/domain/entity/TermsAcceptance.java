package com.ITJobsBackend.authentication.domain.entity;

import com.ITJobsBackend.authentication.domain.aggregate.TermsDocument;
import com.ITJobsBackend.authentication.domain.valueobjects.TermsAcceptanceId;
import com.ITJobsBackend.authentication.domain.valueobjects.TermsDocumentId;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

/**
 * Audit record: a user accepted a specific {@link
 * TermsDocument} at a given point in time.
 * Immutable once created.
 */
public record TermsAcceptance(TermsAcceptanceId id, UserId userId, TermsDocumentId termsDocumentId, Timestamp acceptedAt) {

    /**
   * Records that {@code userId} accepted the document identified by {@code termsDocumentId}
   * at the current UTC instant.
   *
   * @param userId          the user who accepted the document
   * @param termsDocumentId the specific document version that was accepted
   * @return a new immutable {@code TermsAcceptance} with {@code acceptedAt = now()}
   */
  public static TermsAcceptance create(UserId userId, TermsDocumentId termsDocumentId) {
    return new TermsAcceptance(
        TermsAcceptanceId.generate(), userId, termsDocumentId, Timestamp.now());
  }

  /**
   * Rebuilds a {@code TermsAcceptance} from persisted data without side effects.
   *
   * <p>Intended for use exclusively by persistence mappers.
   *
   * @param id              stored acceptance id
   * @param userId          the user who accepted the document
   * @param termsDocumentId the document that was accepted
   * @param acceptedAt      the instant the acceptance was recorded
   * @return a reconstituted {@code TermsAcceptance}
   */
  public static TermsAcceptance reconstitute(
      TermsAcceptanceId id, UserId userId, TermsDocumentId termsDocumentId, Timestamp acceptedAt) {
    return new TermsAcceptance(id, userId, termsDocumentId, acceptedAt);
  }
}
