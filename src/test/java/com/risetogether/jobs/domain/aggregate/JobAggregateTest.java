package com.risetogether.jobs.domain.aggregate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.risetogether.jobs.domain.valueobjects.EmploymentType;
import com.risetogether.jobs.domain.valueobjects.JobStatus;
import com.risetogether.jobs.domain.valueobjects.Salary;
import com.risetogether.jobs.domain.valueobjects.WorkModality;
import com.risetogether.shared.domain.exceptions.ValidationException;

class JobAggregateTest {

  private JobAggregate createOpenJob() {
    return JobAggregate.create(
        "Senior Java Developer",
        "Build amazing things",
        "TechCorp",
        "Remote",
        Salary.of(new BigDecimal("80000"), new BigDecimal("120000"), "USD"),
        EmploymentType.FULL_TIME,
        WorkModality.REMOTE,
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
  void shouldCreateJobWithWorkModality() {
    // When
    JobAggregate job = createOpenJob();

    // Then
    assertEquals(WorkModality.REMOTE, job.getWorkModality());
  }

  @Test
  void shouldDefaultWorkModalityToOnSiteWhenNull() {
    // When
    JobAggregate job =
        JobAggregate.create(
            "Developer",
            "desc",
            "Company",
            "Madrid",
            Salary.of(new BigDecimal("30000"), new BigDecimal("50000"), "EUR"),
            EmploymentType.FULL_TIME,
            null,
            null);

    // Then
    assertEquals(WorkModality.ON_SITE, job.getWorkModality());
  }

  @Test
  void shouldCreateJobWithHybridModality() {
    // When
    JobAggregate job =
        JobAggregate.create(
            "Developer",
            "desc",
            "Company",
            "Barcelona",
            Salary.of(new BigDecimal("40000"), new BigDecimal("60000"), "EUR"),
            EmploymentType.FULL_TIME,
            WorkModality.HYBRID,
            null);

    // Then
    assertEquals(WorkModality.HYBRID, job.getWorkModality());
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
                WorkModality.ON_SITE,
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
                WorkModality.ON_SITE,
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
    var employerId = com.risetogether.shared.domain.valueobjects.EmployerId.generate();
    job.linkToEmployer(employerId);

    // Then
    assertEquals(employerId, job.getEmployerId());
  }

  @Test
  void shouldNotReassignEmployer() {
    // Given
    var employerId1 = com.risetogether.shared.domain.valueobjects.EmployerId.generate();
    var employerId2 = com.risetogether.shared.domain.valueobjects.EmployerId.generate();
    JobAggregate job = createOpenJob();
    job.linkToEmployer(employerId1);

    // When
    job.linkToEmployer(employerId2);

    // Then
    assertEquals(employerId1, job.getEmployerId());
  }
}
