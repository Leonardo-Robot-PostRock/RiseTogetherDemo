package com.risetogether.jobs.application.usecases.addskills;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.risetogether.jobs.domain.aggregate.JobAggregate;
import com.risetogether.jobs.domain.exceptions.JobNotFoundException;
import com.risetogether.jobs.domain.repository.JobReaderRepository;
import com.risetogether.jobs.domain.repository.JobWriterRepository;
import com.risetogether.jobs.domain.valueobjects.EmploymentType;
import com.risetogether.jobs.domain.valueobjects.Salary;
import com.risetogether.jobs.domain.valueobjects.WorkModality;

@ExtendWith(MockitoExtension.class)
class AddSkillsUseCaseTest {

    // ── Mocks (repositorios de dominio) ────────────────────────────────────────
    @Mock private JobReaderRepository jobReaderRepository;
    @Mock private JobWriterRepository jobWriterRepository;

    // ── Subject under test ────────────────────────────────────────────────────
    @InjectMocks private AddSkillsUseCase useCase;

    // ── Helpers ───────────────────────────────────────────────────────────────
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

    // ── Tests ─────────────────────────────────────────────────────────────────
    @Test
    void shouldAddSkillsToJob() {
        // Given
        JobAggregate job = createOpenJob();
        String jobId = job.getId().value().toString();
        given(jobReaderRepository.findById(any())).willReturn(Optional.of(job));
        given(jobWriterRepository.save(any())).willAnswer(i -> i.getArgument(0));

        // When
        useCase.execute(new AddSkillsCommand(jobId, List.of("Java", "Spring")));

        // Then
        ArgumentCaptor<JobAggregate> captor = ArgumentCaptor.forClass(JobAggregate.class);
        then(jobWriterRepository).should().save(captor.capture());
        JobAggregate savedJob = captor.getValue();
        assertTrue(savedJob.getSkills().containsAll(List.of("java", "spring")));
        assertEquals(2, savedJob.getSkills().size());
    }

    @Test
    void shouldThrowExceptionWhenJobNotFound() {
        // Given
        JobAggregate job = createOpenJob();
        String jobId = job.getId().value().toString();
        given(jobReaderRepository.findById(any())).willReturn(Optional.empty());

        // When & Then
        assertThrows(
                JobNotFoundException.class,
                () -> useCase.execute(new AddSkillsCommand(jobId, List.of("Java"))));
    }

    @Test
    void shouldNotAddDuplicateSkills() {
        // Given
        JobAggregate job = createOpenJob();
        String jobId = job.getId().value().toString();
        given(jobReaderRepository.findById(any())).willReturn(Optional.of(job));
        given(jobWriterRepository.save(any())).willAnswer(i -> i.getArgument(0));

        // When
        useCase.execute(new AddSkillsCommand(jobId, List.of("Java", "JAVA", "Spring", "spring")));

        // Then
        ArgumentCaptor<JobAggregate> captor = ArgumentCaptor.forClass(JobAggregate.class);
        then(jobWriterRepository).should().save(captor.capture());
        JobAggregate savedJob = captor.getValue();
        assertTrue(savedJob.getSkills().containsAll(List.of("java", "spring")));
        assertEquals(2, savedJob.getSkills().size());
    }
}
