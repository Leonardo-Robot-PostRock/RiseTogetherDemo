package com.risetogether.jobs.application.usecases.createjob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.risetogether.jobs.application.ports.in.CreateJobPort;
import com.risetogether.jobs.application.usecases.searchjobs.JobResponse;
import com.risetogether.jobs.domain.aggregate.JobAggregate;
import com.risetogether.jobs.domain.repository.JobWriterRepository;
import com.risetogether.jobs.domain.valueobjects.EmploymentType;
import com.risetogether.jobs.domain.valueobjects.Salary;
import com.risetogether.jobs.domain.valueobjects.WorkModality;
import com.risetogether.shared.application.ports.out.DomainEventPublisher;
import com.risetogether.shared.domain.valueobjects.EmployerId;

@Service
@Transactional
public class CreateJobUseCase implements CreateJobPort {
  private static final Logger log = LoggerFactory.getLogger(CreateJobUseCase.class);

  private final JobWriterRepository jobRepository;
  private final DomainEventPublisher domainEventPublisher;

  public CreateJobUseCase(
      JobWriterRepository jobRepository, DomainEventPublisher domainEventPublisher) {
    this.jobRepository = jobRepository;
    this.domainEventPublisher = domainEventPublisher;
  }

  public JobResponse execute(CreateJobCommand command) {
    log.info("Creating new job: {}", command.title());

    Salary salary = Salary.of(command.salaryMin(), command.salaryMax(), command.currency());
    EmploymentType type = EmploymentType.valueOf(command.employmentType().toUpperCase());
    WorkModality modality = command.workModality() != null
        ? WorkModality.valueOf(command.workModality().toUpperCase())
        : WorkModality.ON_SITE;

    EmployerId employerId = null;
    if (command.employerId() != null && !command.employerId().isBlank()) {
      employerId = EmployerId.of(command.employerId());
    }

    JobAggregate job =
        JobAggregate.create(
            command.title(),
            command.description(),
            command.company(),
            command.location(),
            salary,
            type,
            modality,
            employerId);

    JobAggregate savedJob = jobRepository.save(job);
    domainEventPublisher.publishAll(savedJob.pullDomainEvents());

    log.info("Job created successfully with ID: {}", savedJob.getId());

    return new JobResponse(
        savedJob.getId().value().toString(),
        savedJob.getTitle(),
        savedJob.getDescription() != null ? savedJob.getDescription() : "",
        savedJob.getCompany(),
        savedJob.getLocation() != null ? savedJob.getLocation() : "",
        savedJob.getSalary().min(),
        savedJob.getSalary().max(),
        savedJob.getSalary().currency(),
        savedJob.getEmploymentType().name(),
        savedJob.getWorkModality().name(),
        savedJob.getStatus().name(),
        savedJob.getCreatedAt().value(),
        savedJob.getSkills());
  }
}
