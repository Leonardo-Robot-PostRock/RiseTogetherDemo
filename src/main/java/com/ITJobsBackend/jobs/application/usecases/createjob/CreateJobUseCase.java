package com.ITJobsBackend.jobs.application.usecases.createjob;

import com.ITJobsBackend.jobs.domain.aggregate.JobAggregate;
import com.ITJobsBackend.jobs.domain.repository.JobWriterRepository;
import com.ITJobsBackend.jobs.domain.valueobjects.EmploymentType;
import com.ITJobsBackend.jobs.domain.valueobjects.Salary;
import com.ITJobsBackend.shared.application.ports.out.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateJobUseCase {
  private static final Logger log = LoggerFactory.getLogger(CreateJobUseCase.class);

  private final JobWriterRepository jobRepository;
  private final DomainEventPublisher domainEventPublisher;

  public CreateJobUseCase(
      JobWriterRepository jobRepository, DomainEventPublisher domainEventPublisher) {
    this.jobRepository = jobRepository;
    this.domainEventPublisher = domainEventPublisher;
  }

  public JobAggregate execute(CreateJobCommand command) {
    log.info("Creating new job: {}", command.title());

    Salary salary = Salary.of(command.salaryMin(), command.salaryMax(), command.currency());
    EmploymentType type = EmploymentType.valueOf(command.employmentType().toUpperCase());

    JobAggregate job =
        JobAggregate.create(
            command.title(),
            command.description(),
            command.company(),
            command.location(),
            salary,
            type);

    JobAggregate savedJob = jobRepository.save(job);
    domainEventPublisher.publishAll(savedJob.pullDomainEvents());

    log.info("Job created successfully with ID: {}", savedJob.getId());

    return savedJob;
  }
}
