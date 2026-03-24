package com.ITJobsBackend.jobs.application.usecases.createjob;

import com.ITJobsBackend.jobs.domain.model.Job;
import com.ITJobsBackend.jobs.domain.repository.JobRepository;
import com.ITJobsBackend.jobs.domain.valueobjects.EmploymentType;
import com.ITJobsBackend.jobs.domain.valueobjects.Salary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateJobUseCase {
    private static final Logger log = LoggerFactory.getLogger(CreateJobUseCase.class);

    private final JobRepository jobRepository;

    public CreateJobUseCase(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job execute(CreateJobCommand command) {
        log.info("Creating new job: {}", command.title());

        Salary salary = Salary.of(command.salaryMin(), command.salaryMax(), command.currency());
        EmploymentType type = EmploymentType.valueOf(command.employmentType().toUpperCase());

        Job job = Job.create(
            command.title(),
            command.description(),
            command.company(),
            command.location(),
            salary,
            type
        );

        Job savedJob = jobRepository.save(job);

        log.info("Job created successfully with ID: {}", savedJob.getId());

        return savedJob;
    }
}
