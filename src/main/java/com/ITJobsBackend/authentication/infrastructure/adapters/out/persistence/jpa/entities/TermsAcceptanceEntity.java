package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
    name = "user_terms_acceptances",
    indexes = @Index(name = "idx_uta_user_id", columnList = "user_id"))
@Getter
@NoArgsConstructor
public class TermsAcceptanceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    @Setter
    private UUID id;

    @Setter
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID userId;

    @Setter
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "terms_document_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID termsDocumentId;

    @Setter
    @Column(name = "accepted_at", nullable = false, updatable = false)
    private Instant acceptedAt;
}

