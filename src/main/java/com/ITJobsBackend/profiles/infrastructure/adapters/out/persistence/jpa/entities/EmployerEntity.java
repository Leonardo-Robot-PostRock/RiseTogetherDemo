package com.ITJobsBackend.profiles.infrastructure.adapters.out.persistence.jpa.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "employers")
@Getter
@Setter
@NoArgsConstructor
public class EmployerEntity {
  @Id
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(columnDefinition = "CHAR(36)")
  private UUID id;

  @Column(name = "company_name", nullable = false, length = 100)
  private String companyName;

  @Column(length = 50)
  private String industry;

  @Column(length = 255)
  private String website;

  @Column(length = 100)
  private String location;

  @Column(name = "contact_person", length = 100)
  private String contactPerson;

  @Column(name = "contact_email", length = 255)
  private String contactEmail;

  @Column(name = "logo_url", length = 500)
  private String logoUrl;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "company_size", length = 20)
  private String companySize;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;
}