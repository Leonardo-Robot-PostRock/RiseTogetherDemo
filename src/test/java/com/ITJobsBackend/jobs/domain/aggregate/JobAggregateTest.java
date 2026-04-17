package com.ITJobsBackend.jobs.domain.aggregate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.ITJobsBackend.jobs.domain.valueobjects.EmploymentType;
import com.ITJobsBackend.jobs.domain.valueobjects.JobStatus;
import com.ITJobsBackend.jobs.domain.valueobjects.Salary;
import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

class JobAggregateTest {

  private JobAggregate createOpenJob() {
    return JobAggregate.create(
        "Senior Java Developer",
        "Build amazing things",
        "TechCorp",
        "Remote",
        Salary.of(new BigDecimal("80000"), new BigDecimal("120000"), "USD"),
        EmploymentType.FULL_TIME,
        null);
  }

  @Test
  void shouldCreateJobWithDefaultStatus() {
    // When
    JobAggregate job = createOpenJob();

    // Then
    assertNotNull(job.getId());
    assertEquals("Senior Java Developer", job.getTitle());
    assertEquals("TechCorp", job.getCompany());
    assertEquals(JobStatus.OPEN, job.getStatus());
    assertTrue(job.getSkills().isEmpty());
    assertNull(job.getEmployerId());
  }

  @Test
  void shouldThrowWhenTitleIsNull() {
    // When & Then
    assertThrows(
        ValidationException.class,
        () ->
            JobAggregate.create(
                null,
                "desc",
                "company",
                "loc",
                Salary.of(BigDecimal.ONE, BigDecimal.TEN, "USD"),
                EmploymentType.FULL_TIME,
                null));
  }

  @Test
  void shouldThrowWhenCompanyIsBlank() {
    // When & Then
    assertThrows(
        ValidationException.class,
        () ->
            JobAggregate.create(
                "title",
                "desc",
                "  ",
                "loc",
                Salary.of(BigDecimal.ONE, BigDecimal.TEN, "USD"),
                EmploymentType.FULL_TIME,
                null));
  }

  @Test
  void shouldCloseOpenJob() {
    // Given
    JobAggregate job = createOpenJob();

    // When
    job.close();

    // Then
    assertEquals(JobStatus.CLOSED, job.getStatus());
  }

  @Test
  void shouldThrowWhenClosingAlreadyClosedJob() {
    // Given
    JobAggregate job = createOpenJob();
    job.close();

    // When & Then
    assertThrows(ValidationException.class, job::close);
  }

  @Test
  void shouldThrowWhenClosingInactiveJob() {
    // Given
    JobAggregate job = createOpenJob();
    job.deactivate();

    // When & Then
    assertThrows(ValidationException.class, job::close);
  }

  @Test
  void shouldDeactivateOpenJob() {
    // Given
    JobAggregate job = createOpenJob();

    // When
    job.deactivate();

    // Then
    assertEquals(JobStatus.INACTIVE, job.getStatus());
  }

  @Test
  void shouldThrowWhenDeactivatingAlreadyInactiveJob() {
    // Given
    JobAggregate job = createOpenJob();
    job.deactivate();

    // When & Then
    assertThrows(ValidationException.class, job::deactivate);
  }

  @Test
  void shouldThrowWhenDeactivatingClosedJob() {
    // Given
    JobAggregate job = createOpenJob();
    job.close();

    // When & Then
    assertThrows(ValidationException.class, job::deactivate);
  }

  @Test
  void shouldAddSkill() {
    // Given
    JobAggregate job = createOpenJob();

    // When
    job.addSkill("Java");

    // Then
    assertTrue(job.getSkills().contains("java"));
  }

  @Test
  void shouldNotAddDuplicateSkill() {
    // Given
    JobAggregate job = createOpenJob();

    // When
    job.addSkill("Java");
    job.addSkill("JAVA");

    // Then
    assertEquals(1, job.getSkills().size());
  }

  @Test
  void shouldRecordDomainEventOnClose() {
    // Given
    JobAggregate job = createOpenJob();

    // When
    job.close();

    // Then
    assertFalse(job.pullDomainEvents().isEmpty());
  }

  @Test
  void shouldRecordDomainEventOnDeactivate() {
    // Given
    JobAggregate job = createOpenJob();

    // When
    job.deactivate();

    // Then
    assertFalse(job.pullDomainEvents().isEmpty());
  }

  @Test
  void shouldLinkToEmployer() {
    // Given
    JobAggregate job = createOpenJob();

    // When
    var employerId = com.ITJobsBackend.shared.domain.valueobjects.EmployerId.generate();
    job.linkToEmployer(employerId);

    // Then
    assertEquals(employerId, job.getEmployerId());
  }

  @Test
  void shouldNotReassignEmployer() {
    // Given
    var employerId1 = com.ITJobsBackend.shared.domain.valueobjects.EmployerId.generate();
    var employerId2 = com.ITJobsBackend.shared.domain.valueobjects.EmployerId.generate();
    JobAggregate job = createOpenJob();
    job.linkToEmployer(employerId1);

    // When
    job.linkToEmployer(employerId2);

    // Then
    assertEquals(employerId1, job.getEmployerId());
  }
}
