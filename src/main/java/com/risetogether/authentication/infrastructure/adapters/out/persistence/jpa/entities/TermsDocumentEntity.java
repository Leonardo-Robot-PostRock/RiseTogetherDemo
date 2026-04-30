package com.risetogether.authentication.infrastructure.adapters.out.persistence.jpa.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.risetogether.authentication.domain.valueobjects.TermsType;

@Entity
@Table(
    name = "terms_documents",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_terms_type_version",
            columnNames = {"terms_type", "version"}))
@Getter
@Setter
@NoArgsConstructor
public class TermsDocumentEntity {

  @Id
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(columnDefinition = "CHAR(36)")
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(name = "terms_type", nullable = false, length = 30)
  private TermsType termsType;

  @Column(nullable = false, length = 10)
  private String version;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "published_at", nullable = false, updatable = false)
  private Instant publishedAt;
}
