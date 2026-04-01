package com.ITJobsBackend.jobs.application.usecases.searchjobs;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ITJobsBackend.jobs.domain.aggregate.JobAggregate;
import com.ITJobsBackend.jobs.domain.repository.JobReaderRepository;
import com.ITJobsBackend.jobs.domain.specification.TitleContainsSpecification;

@Service
@Transactional(readOnly = true)
public class SearchJobsUseCase {
  private final JobReaderRepository jobRepository;

  public SearchJobsUseCase(JobReaderRepository jobRepository) {
    this.jobRepository = jobRepository;
  }

  public List<JobAggregate> execute(SearchJobsQuery query) {
    if (query.title() != null && !query.title().isBlank()) {
      return jobRepository.findAll(new TitleContainsSpecification(query.title()));
    }
    return jobRepository.findAll();
  }
}
