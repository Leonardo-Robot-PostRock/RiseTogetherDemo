package com.ITJobsBackend.jobs.infrastructure.adapters.out.persistence.jpa.entities;

import com.ITJobsBackend.jobs.domain.valueobjects.EmploymentType;
import com.ITJobsBackend.jobs.domain.valueobjects.JobStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
    name = "jobs",
    indexes = {
      @Index(name = "idx_job_title", columnList = "title"),
      @Index(name = "idx_job_company", columnList = "company")
    })
@Getter
@NoArgsConstructor
public class JobEntity {
  @Id
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(columnDefinition = "CHAR(36)")
  @Setter
  private UUID id;

  @Setter
  @Column(nullable = false, length = 200)
  private String title;

  @Setter
  @Column(columnDefinition = "TEXT")
  private String description;

  @Setter
  @Column(nullable = false, length = 100)
  private String company;

  @Setter
  @Column(length = 100)
  private String location;

  @Setter
  @Column(name = "salary_min", precision = 15, scale = 2)
  private BigDecimal salaryMin;

  @Setter
  @Column(name = "salary_max", precision = 15, scale = 2)
  private BigDecimal salaryMax;

  @Setter
  @Column(length = 10)
  private String currency;

  @Setter
  @Column(name = "employment_type", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private EmploymentType employmentType;

  @Setter
  @Column(nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private JobStatus status;

  @Setter
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
  @Column(name = "skill")
  private List<String> skills = new ArrayList<>();

  @Setter
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Setter
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;
}
