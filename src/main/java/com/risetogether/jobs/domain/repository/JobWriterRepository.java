package com.risetogether.jobs.domain.repository;

import com.risetogether.jobs.domain.aggregate.JobAggregate;

/**
 * Write-side repository port for {@link JobAggregate}.
 *
 * <p>Implementations live in the infrastructure layer (e.g. {@code JpaJobRepositoryAdapter}).
 */
public interface JobWriterRepository {

  /**
   * Persists a new or updated {@link JobAggregate}.
   *
   * @param job the aggregate to save
   * @return the saved aggregate (may include database-generated values)
   */
  JobAggregate save(JobAggregate job);
}
