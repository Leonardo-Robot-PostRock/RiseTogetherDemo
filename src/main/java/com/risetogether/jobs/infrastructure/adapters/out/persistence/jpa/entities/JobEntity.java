package com.risetogether.jobs.infrastructure.adapters.out.persistence.jpa.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.risetogether.jobs.domain.valueobjects.EmploymentType;
import com.risetogether.jobs.domain.valueobjects.JobStatus;
import com.risetogether.jobs.domain.valueobjects.WorkModality;
import com.risetogether.profiles.infrastructure.adapters.out.persistence.jpa.entities.EmployerEntity;

@Entity
@Table(
    name = "jobs",
    indexes = {
      @Index(name = "idx_job_title", columnList = "title"),
      @Index(name = "idx_job_company", columnList = "company")
    })
@Getter
@Setter
@NoArgsConstructor
public class JobEntity {
  @Id
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(columnDefinition = "CHAR(36)")
  private UUID id;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false, length = 100)
  private String company;

  @Column(length = 100)
  private String location;

  @Column(name = "salary_min", precision = 15, scale = 2)
  private BigDecimal salaryMin;

  @Column(name = "salary_max", precision = 15, scale = 2)
  private BigDecimal salaryMax;

  @Column(length = 10)
  private String currency;

  @Column(name = "employment_type", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private EmploymentType employmentType;

  @Column(name = "work_modality", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private WorkModality workModality;

  @Column(nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private JobStatus status;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
  @Column(name = "skill")
  private List<String> skills = new ArrayList<>();

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "employer_id", columnDefinition = "CHAR(36)")
  private UUID employerId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employer_id", insertable = false, updatable = false)
  private EmployerEntity employer;
}
