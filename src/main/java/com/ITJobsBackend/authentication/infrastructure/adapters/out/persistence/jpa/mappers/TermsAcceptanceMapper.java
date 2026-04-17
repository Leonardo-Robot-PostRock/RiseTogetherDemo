package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.mappers;

import org.springframework.stereotype.Component;

import com.ITJobsBackend.authentication.domain.entity.TermsAcceptance;
import com.ITJobsBackend.authentication.domain.valueobjects.TermsAcceptanceId;
import com.ITJobsBackend.authentication.domain.valueobjects.TermsDocumentId;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.entities.TermsAcceptanceEntity;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@Component
public class TermsAcceptanceMapper {

    public TermsAcceptance toDomain(TermsAcceptanceEntity entity) {
        return TermsAcceptance.reconstitute(
                TermsAcceptanceId.of(entity.getId()),
                UserId.of(entity.getUserId()),
                TermsDocumentId.of(entity.getTermsDocumentId()),
                Timestamp.of(entity.getAcceptedAt()));
    }

    public TermsAcceptanceEntity toEntity(TermsAcceptance domain) {
        TermsAcceptanceEntity entity = new TermsAcceptanceEntity();
        entity.setId(domain.getId().value());
        entity.setUserId(domain.getUserId().value());
        entity.setTermsDocumentId(domain.getTermsDocumentId().value());
        entity.setAcceptedAt(domain.getAcceptedAt().value());
        return entity;
    }
}

