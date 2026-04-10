package com.ITJobsBackend.jobs.domain.aggregate;

import com.ITJobsBackend.jobs.domain.event.JobClosedEvent;
import com.ITJobsBackend.jobs.domain.event.JobCreatedEvent;
import com.ITJobsBackend.jobs.domain.event.JobDeactivatedEvent;
import com.ITJobsBackend.jobs.domain.valueobjects.EmploymentType;
import com.ITJobsBackend.jobs.domain.valueobjects.JobId;
import com.ITJobsBackend.jobs.domain.valueobjects.JobStatus;
import com.ITJobsBackend.jobs.domain.valueobjects.Salary;
import com.ITJobsBackend.shared.domain.AggregateRoot;
import com.ITJobsBackend.shared.domain.exceptions.ValidationException;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JobAggregate extends AggregateRoot {
  private final JobId id;
  private String title;
  private String description;
  private String company;
  private String location;
  private Salary salary;
  private EmploymentType employmentType;
  private JobStatus status;
  private final List<String> skills;
  private final Timestamp createdAt;
  private Timestamp updatedAt;

  private JobAggregate(
      JobId id,
      String title,
      String description,
      String company,
      String location,
      Salary salary,
      EmploymentType employmentType,
      Timestamp createdAt) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.company = company;
    this.location = location;
    this.salary = salary;
    this.employmentType = employmentType;
    this.status = JobStatus.OPEN;
    this.skills = new ArrayList<>();
    this.createdAt = createdAt;
    this.updatedAt = createdAt;
  }

  public static JobAggregate create(
      String title,
      String description,
      String company,
      String location,
      Salary salary,
      EmploymentType employmentType) {
    if (title == null || title.isBlank()) {
      throw new ValidationException("Job title cannot be empty");
    }
    if (company == null || company.isBlank()) {
      throw new ValidationException("Company name cannot be empty");
    }

    return new JobAggregate(
        JobId.generate(),
        title,
        description,
        company,
        location,
        salary,
        employmentType,
        Timestamp.now());
  }

  public static JobAggregate reconstitute(
      JobId id,
      String title,
      String description,
      String company,
      String location,
      Salary salary,
      EmploymentType employmentType,
      JobStatus status,
      List<String> skills,
      Timestamp createdAt,
      Timestamp updatedAt) {
    JobAggregate job =
        new JobAggregate(
            id, title, description, company, location, salary, employmentType, createdAt);
    job.status = status;
    job.skills.clear();
    job.skills.addAll(skills);
    job.updatedAt = updatedAt;

    job.recordEvent(new JobCreatedEvent(job.id.value().toString(), title, company));
    return job;
  }

  public void close() {
    if (this.status == JobStatus.CLOSED) {
      throw new ValidationException("Job is already closed");
    }
    if (this.status == JobStatus.INACTIVE) {
      throw new ValidationException("Cannot close an inactive job");
    }
    this.status = JobStatus.CLOSED;
    this.updatedAt = Timestamp.now();
    recordEvent(new JobClosedEvent(this.id.value().toString(), this.title, this.company));
  }

  public void deactivate() {
    if (this.status == JobStatus.INACTIVE) {
      throw new ValidationException("Job is already inactive");
    }
    if (this.status == JobStatus.CLOSED) {
      throw new ValidationException("Cannot deactivate a closed job");
    }
    this.status = JobStatus.INACTIVE;
    this.updatedAt = Timestamp.now();
    recordEvent(new JobDeactivatedEvent(this.id.value().toString(), this.title));
  }

  public void addSkill(String skill) {
    if (skill == null || skill.isBlank()) {
      return;
    }

    String normalized = skill.trim().toLowerCase();

    if (!this.skills.contains(normalized)) {
      this.skills.add(normalized);
      this.updatedAt = Timestamp.now();
    }
  }

  public JobId getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getCompany() {
    return company;
  }

  public String getLocation() {
    return location;
  }

  public Salary getSalary() {
    return salary;
  }

  public EmploymentType getEmploymentType() {
    return employmentType;
  }

  public JobStatus getStatus() {
    return status;
  }

  public List<String> getSkills() {
    return Collections.unmodifiableList(skills);
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }
}
