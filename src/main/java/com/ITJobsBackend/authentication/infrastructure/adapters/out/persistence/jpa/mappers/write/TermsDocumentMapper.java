package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.mappers.write;

import org.springframework.stereotype.Component;

import com.ITJobsBackend.authentication.domain.aggregate.TermsDocument;
import com.ITJobsBackend.authentication.domain.valueobjects.TermsDocumentId;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.entities.TermsDocumentEntity;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;

@Component
public class TermsDocumentMapper {

  public TermsDocument toDomain(TermsDocumentEntity entity) {
    return TermsDocument.reconstitute(
        TermsDocumentId.of(entity.getId()),
        entity.getTermsType(),
        entity.getVersion(),
        entity.getContent(),
        Timestamp.of(entity.getPublishedAt()));
  }

  public TermsDocumentEntity toEntity(TermsDocument domain) {
    TermsDocumentEntity entity = new TermsDocumentEntity();
    entity.setId(domain.getId().value());
    entity.setTermsType(domain.getTermsType());
    entity.setVersion(domain.getVersion());
    entity.setContent(domain.getContent());
    entity.setPublishedAt(domain.getPublishedAt().value());
    return entity;
  }
}
