package com.ITJobsBackend.jobs.infrastructure.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "jobs", indexes = {
    @Index(name = "idx_job_title", columnList = "title"),
    @Index(name = "idx_job_company", columnList = "company")
})
public class JobEntity {
    @Id
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

    @Column(name = "salary_min")
    private double salaryMin;

    @Column(name = "salary_max")
    private double salaryMax;

    @Column(length = 10)
    private String currency;

    @Column(name = "employment_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private com.ITJobsBackend.jobs.domain.valueobjects.EmploymentType employmentType;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private com.ITJobsBackend.jobs.domain.valueobjects.JobStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "job_skills",
        joinColumns = @JoinColumn(name = "job_id")
    )
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public JobEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getSalaryMin() { return salaryMin; }
    public void setSalaryMin(double salaryMin) { this.salaryMin = salaryMin; }

    public double getSalaryMax() { return salaryMax; }
    public void setSalaryMax(double salaryMax) { this.salaryMax = salaryMax; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public com.ITJobsBackend.jobs.domain.valueobjects.EmploymentType getEmploymentType() { return employmentType; }
    public void setEmploymentType(com.ITJobsBackend.jobs.domain.valueobjects.EmploymentType employmentType) { this.employmentType = employmentType; }

    public com.ITJobsBackend.jobs.domain.valueobjects.JobStatus getStatus() { return status; }
    public void setStatus(com.ITJobsBackend.jobs.domain.valueobjects.JobStatus status) { this.status = status; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
