package com.ITJobsBackend.jobs.domain.specification;

import com.ITJobsBackend.jobs.domain.aggregate.JobAggregate;

public class TitleContainsSpecification implements JobSpecification {
  private final String keyword;

  public TitleContainsSpecification(String keyword) {
    this.keyword = keyword.toLowerCase();
  }

  @Override
  public boolean isSatisfiedBy(JobAggregate job) {
    return job.getTitle().toLowerCase().contains(keyword);
  }
}
