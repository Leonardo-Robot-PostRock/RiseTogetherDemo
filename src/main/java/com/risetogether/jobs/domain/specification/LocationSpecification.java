package com.risetogether.jobs.domain.specification;

import com.risetogether.jobs.domain.aggregate.JobAggregate;

/**
 * {@link JobSpecification} that matches jobs whose location contains the given keyword
 * (case-insensitive substring match). Jobs with a {@code null} location never satisfy this
 * specification.
 */
public record LocationSpecification(String location) implements JobSpecification {
  /**
   * @param location the location keyword to match (case-insensitive substring)
   */
  public LocationSpecification(String location) {
    this.location = location.toLowerCase();
  }

  @Override
  public boolean isSatisfiedBy(JobAggregate job) {
    return job.getLocation() != null && job.getLocation().toLowerCase().contains(location);
  }
}
