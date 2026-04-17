package com.ITJobsBackend.jobs.application.usecases.searchjobs;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ITJobsBackend.jobs.application.ports.in.SearchJobsPort;
import com.ITJobsBackend.jobs.domain.aggregate.JobAggregate;
import com.ITJobsBackend.jobs.domain.repository.JobReaderRepository;
import com.ITJobsBackend.jobs.domain.specification.TitleContainsSpecification;

@Service
@Transactional(readOnly = true)
public class SearchJobsUseCase implements SearchJobsPort {
  private final JobReaderRepository jobRepository;

  public SearchJobsUseCase(JobReaderRepository jobRepository) {
    this.jobRepository = jobRepository;
  }

  public List<JobResponse> execute(SearchJobsQuery query) {
    List<JobAggregate> jobs;
    if (query.title() != null && !query.title().isBlank()) {
      jobs = jobRepository.findAll(new TitleContainsSpecification(query.title()));
    } else {
      jobs = jobRepository.findAll();
    }
    return jobs.stream().map(this::toResponse).collect(Collectors.toList());
  }

  private JobResponse toResponse(JobAggregate job) {
    return new JobResponse(
        job.getId().value().toString(),
        job.getTitle(),
        job.getDescription() != null ? job.getDescription() : "",
        job.getCompany(),
        job.getLocation() != null ? job.getLocation() : "",
        job.getSalary().min(),
        job.getSalary().max(),
        job.getSalary().currency(),
        job.getEmploymentType().name(),
        job.getWorkModality().name(),
        job.getStatus().name(),
        job.getCreatedAt().value());
  }
}
