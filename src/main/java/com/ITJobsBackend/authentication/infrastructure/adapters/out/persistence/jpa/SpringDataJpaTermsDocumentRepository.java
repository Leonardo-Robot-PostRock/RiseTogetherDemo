package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ITJobsBackend.authentication.domain.valueobjects.TermsType;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.entities.TermsDocumentEntity;

public interface SpringDataJpaTermsDocumentRepository
        extends JpaRepository<TermsDocumentEntity, UUID> {

    @Query("SELECT t FROM TermsDocumentEntity t WHERE t.termsType = :type ORDER BY t.publishedAt DESC LIMIT 1")
    Optional<TermsDocumentEntity> findLatestByTermsType(@Param("type") TermsType type);
}

