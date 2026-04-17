package com.ITJobsBackend.authentication.domain.entity;

import com.ITJobsBackend.authentication.domain.valueobjects.TermsAcceptanceId;
import com.ITJobsBackend.authentication.domain.valueobjects.TermsDocumentId;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

/**
 * Audit record: a user accepted a specific {@link com.ITJobsBackend.authentication.domain.aggregate.TermsDocument}
 * at a given point in time. Immutable once created.
 */
public class TermsAcceptance {

    private final TermsAcceptanceId id;
    private final UserId userId;
    private final TermsDocumentId termsDocumentId;
    private final Timestamp acceptedAt;

    private TermsAcceptance(
            TermsAcceptanceId id,
            UserId userId,
            TermsDocumentId termsDocumentId,
            Timestamp acceptedAt) {
        this.id = id;
        this.userId = userId;
        this.termsDocumentId = termsDocumentId;
        this.acceptedAt = acceptedAt;
    }

    public static TermsAcceptance create(UserId userId, TermsDocumentId termsDocumentId) {
        return new TermsAcceptance(
                TermsAcceptanceId.generate(), userId, termsDocumentId, Timestamp.now());
    }

    public static TermsAcceptance reconstitute(
            TermsAcceptanceId id,
            UserId userId,
            TermsDocumentId termsDocumentId,
            Timestamp acceptedAt) {
        return new TermsAcceptance(id, userId, termsDocumentId, acceptedAt);
    }

    public TermsAcceptanceId getId() { return id; }
    public UserId getUserId() { return userId; }
    public TermsDocumentId getTermsDocumentId() { return termsDocumentId; }
    public Timestamp getAcceptedAt() { return acceptedAt; }
}

