package com.ITJobsBackend.jobs.domain.aggregate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ITJobsBackend.jobs.domain.event.JobClosedEvent;
import com.ITJobsBackend.jobs.domain.event.JobCreatedEvent;
import com.ITJobsBackend.jobs.domain.event.JobDeactivatedEvent;
import com.ITJobsBackend.jobs.domain.valueobjects.EmploymentType;
import com.ITJobsBackend.jobs.domain.valueobjects.JobId;
import com.ITJobsBackend.jobs.domain.valueobjects.JobStatus;
import com.ITJobsBackend.jobs.domain.valueobjects.Salary;
import com.ITJobsBackend.jobs.domain.valueobjects.WorkModality;
import com.ITJobsBackend.shared.domain.AggregateRoot;
import com.ITJobsBackend.shared.domain.exceptions.ValidationException;
import com.ITJobsBackend.shared.domain.valueobjects.EmployerId;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;

/**
 * Aggregate root for the {@code jobs} bounded context.
 *
 * <p>Represents a job listing posted on the platform. A job starts in {@link JobStatus#OPEN}
 * and transitions to {@link JobStatus#CLOSED} or {@link JobStatus#INACTIVE} through explicit
 * lifecycle commands.
 *
 * <h2>Factory methods</h2>
 * <ul>
 *   <li>{@link #create} — publishes a new open job, fires {@link JobCreatedEvent}</li>
 *   <li>{@link #reconstitute} — rebuilds from persistence (fires {@link JobCreatedEvent} as a
 *       side effect — see implementation note)</li>
 * </ul>
 *
 * <h2>Domain events raised</h2>
 * <ul>
 *   <li>{@link JobCreatedEvent} — from {@link #reconstitute} (and implicitly on creation)</li>
 *   <li>{@link JobClosedEvent} — from {@link #close()}</li>
 *   <li>{@link JobDeactivatedEvent} — from {@link #deactivate()}</li>
 * </ul>
 */
public class JobAggregate extends AggregateRoot {
  private final JobId id;
  private final List<String> skills;
  private final Timestamp createdAt;
  private String title;
  private String description;
  private String company;
  private String location;
  private Salary salary;
  private EmploymentType employmentType;
  private JobStatus status;
  private WorkModality workModality;
  private Timestamp updatedAt;
  private EmployerId employerId;

  private JobAggregate(
      JobId id,
      String title,
      String description,
      String company,
      String location,
      Salary salary,
      EmploymentType employmentType,
      WorkModality workModality,
      Timestamp createdAt,
      EmployerId employerId) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.company = company;
    this.location = location;
    this.salary = salary;
    this.employmentType = employmentType;
    this.workModality = workModality;
    this.status = JobStatus.OPEN;
    this.skills = new ArrayList<>();
    this.createdAt = createdAt;
    this.updatedAt = createdAt;
    this.employerId = employerId;
  }

  /**
   * Creates and publishes a new job listing in {@link JobStatus#OPEN} status.
   *
   * @param title          job title; must not be blank
   * @param description    optional job description
   * @param company        company name; must not be blank
   * @param location       optional location string
   * @param salary         salary range
   * @param employmentType employment type (full-time, contract, etc.)
   * @param workModality   work modality; defaults to {@link WorkModality#ON_SITE} if {@code null}
   * @param employerId     the employer who owns this listing (may be {@code null} if not yet linked)
   * @return a new open {@code JobAggregate}
   * @throws ValidationException if title or company is blank
   */
  public static JobAggregate create(
      String title,
      String description,
      String company,
      String location,
      Salary salary,
      EmploymentType employmentType,
      WorkModality workModality,
      EmployerId employerId) {
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
        workModality != null ? workModality : WorkModality.ON_SITE,
        Timestamp.now(),
        employerId);
  }

  /**
   * Rebuilds a {@code JobAggregate} from persisted data.
   *
   * <p><b>Note:</b> this method records a {@link JobCreatedEvent} as part of its current
   * implementation. This is a known design inconsistency and will be addressed in a future
   * refactor.
   *
   * @param id             stored job id
   * @param title          stored title
   * @param description    stored description
   * @param company        stored company name
   * @param location       stored location
   * @param salary         stored salary range
   * @param employmentType stored employment type
   * @param workModality   stored work modality; defaults to {@link WorkModality#ON_SITE} if {@code null}
   * @param status         stored job status
   * @param skills         stored skill list
   * @param createdAt      stored creation timestamp
   * @param updatedAt      stored last-updated timestamp
   * @param employerId     stored employer id (may be {@code null})
   * @return a reconstituted {@code JobAggregate}
   */
  public static JobAggregate reconstitute(
      JobId id,
      String title,
      String description,
      String company,
      String location,
      Salary salary,
      EmploymentType employmentType,
      WorkModality workModality,
      JobStatus status,
      List<String> skills,
      Timestamp createdAt,
      Timestamp updatedAt,
      EmployerId employerId) {
    JobAggregate job =
        new JobAggregate(
            id,
            title,
            description,
            company,
            location,
            salary,
            employmentType,
            workModality != null ? workModality : WorkModality.ON_SITE,
            createdAt,
            employerId);
    job.status = status;
    job.skills.clear();
    job.skills.addAll(skills);
    job.updatedAt = updatedAt;

    job.recordEvent(new JobCreatedEvent(job.id, title, company));
    return job;
  }

  /**
   * Closes the job listing.
   *
   * <p>Fires {@link JobClosedEvent}.
   *
   * @throws ValidationException if the job is already closed or inactive
   */
  public void close() {
    if (this.status == JobStatus.CLOSED) {
      throw new ValidationException("Job is already closed");
    }
    if (this.status == JobStatus.INACTIVE) {
      throw new ValidationException("Cannot close an inactive job");
    }
    this.status = JobStatus.CLOSED;
    this.updatedAt = Timestamp.now();

    recordEvent(new JobClosedEvent(this.id, this.title, this.company));
  }

  /**
   * Deactivates the job listing (e.g. temporarily hidden from search).
   *
   * <p>Fires {@link JobDeactivatedEvent}.
   *
   * @throws ValidationException if the job is already inactive or closed
   */
  public void deactivate() {
    if (this.status == JobStatus.INACTIVE) {
      throw new ValidationException("Job is already inactive");
    }
    if (this.status == JobStatus.CLOSED) {
      throw new ValidationException("Cannot deactivate a closed job");
    }
    this.status = JobStatus.INACTIVE;
    this.updatedAt = Timestamp.now();

    recordEvent(new JobDeactivatedEvent(this.id, this.title));
  }

  /**
   * Adds a required skill to this job if not already present.
   *
   * <p>The skill is normalised (trimmed and lower-cased) before being added.
   * Blank values are silently ignored.
   *
   * @param skill the skill string to add (e.g. {@code "Java"})
   */
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

  public WorkModality getWorkModality() {
    return workModality;
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

  public EmployerId getEmployerId() {
    return employerId;
  }

  /**
   * Links this job to an {@link com.ITJobsBackend.profiles.domain.aggregate.EmployerAggregate} if not already linked.
   *
   * <p>Silently ignored if the job already has an employer id assigned.
   *
   * @param employerId the employer id to associate with this job
   */
  public void linkToEmployer(EmployerId employerId) {
    if (this.employerId == null) {
      this.employerId = employerId;
      this.updatedAt = Timestamp.now();
    }
  }
}
