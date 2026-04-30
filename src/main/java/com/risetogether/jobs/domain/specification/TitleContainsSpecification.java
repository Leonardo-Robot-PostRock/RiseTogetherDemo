package com.risetogether.jobs.domain.specification;

import com.risetogether.jobs.domain.aggregate.JobAggregate;

/**
 * {@link JobSpecification} that matches jobs whose title contains a given keyword (case-insensitive
 * substring match).
 */
public record TitleContainsSpecification(String keyword) implements JobSpecification {
  /**
   * @param keyword the keyword to search for in the job title (matched case-insensitively)
   */
  public TitleContainsSpecification(String keyword) {
    this.keyword = keyword.toLowerCase();
  }

  @Override
  public boolean isSatisfiedBy(JobAggregate job) {
    return job.getTitle().toLowerCase().contains(keyword);
  }
}
