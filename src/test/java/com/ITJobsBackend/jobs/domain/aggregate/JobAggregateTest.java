package com.ITJobsBackend.jobs.domain.aggregate;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
            EmploymentType.FULL_TIME
        );
    }

    @Test
    void shouldCreateJobWithDefaultStatus() {
        JobAggregate job = createOpenJob();

        assertNotNull(job.getId());
        assertEquals("Senior Java Developer", job.getTitle());
        assertEquals("TechCorp", job.getCompany());
        assertEquals(JobStatus.OPEN, job.getStatus());
        assertTrue(job.getSkills().isEmpty());
    }

    @Test
    void shouldThrowWhenTitleIsNull() {
        assertThrows(ValidationException.class, () ->
            JobAggregate.create(null, "desc", "company", "loc",
                Salary.of(BigDecimal.ONE, BigDecimal.TEN, "USD"), EmploymentType.FULL_TIME)
        );
    }

    @Test
    void shouldThrowWhenCompanyIsBlank() {
        assertThrows(ValidationException.class, () ->
            JobAggregate.create("title", "desc", "  ", "loc",
                Salary.of(BigDecimal.ONE, BigDecimal.TEN, "USD"), EmploymentType.FULL_TIME)
        );
    }

    @Test
    void shouldCloseOpenJob() {
        JobAggregate job = createOpenJob();
        job.close();
        assertEquals(JobStatus.CLOSED, job.getStatus());
    }

    @Test
    void shouldThrowWhenClosingAlreadyClosedJob() {
        JobAggregate job = createOpenJob();
        job.close();
        assertThrows(ValidationException.class, job::close);
    }

    @Test
    void shouldThrowWhenClosingInactiveJob() {
        JobAggregate job = createOpenJob();
        job.deactivate();
        assertThrows(ValidationException.class, job::close);
    }

    @Test
    void shouldDeactivateOpenJob() {
        JobAggregate job = createOpenJob();
        job.deactivate();
        assertEquals(JobStatus.INACTIVE, job.getStatus());
    }

    @Test
    void shouldThrowWhenDeactivatingAlreadyInactiveJob() {
        JobAggregate job = createOpenJob();
        job.deactivate();
        assertThrows(ValidationException.class, job::deactivate);
    }

    @Test
    void shouldThrowWhenDeactivatingClosedJob() {
        JobAggregate job = createOpenJob();
        job.close();
        assertThrows(ValidationException.class, job::deactivate);
    }

    @Test
    void shouldAddSkill() {
        JobAggregate job = createOpenJob();
        job.addSkill("Java");
        assertTrue(job.getSkills().contains("java"));
    }

    @Test
    void shouldNotAddDuplicateSkill() {
        JobAggregate job = createOpenJob();
        job.addSkill("Java");
        job.addSkill("JAVA");
        assertEquals(1, job.getSkills().size());
    }

    @Test
    void shouldRecordDomainEventOnClose() {
        JobAggregate job = createOpenJob();
        job.close();
        assertFalse(job.pullDomainEvents().isEmpty());
    }

    @Test
    void shouldRecordDomainEventOnDeactivate() {
        JobAggregate job = createOpenJob();
        job.deactivate();
        assertFalse(job.pullDomainEvents().isEmpty());
    }
}
