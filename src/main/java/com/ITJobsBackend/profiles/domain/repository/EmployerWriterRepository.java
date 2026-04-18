package com.ITJobsBackend.profiles.domain.repository;

import com.ITJobsBackend.profiles.domain.aggregate.EmployerAggregate;

/**
 * Write-side repository port for {@link EmployerAggregate}.
 *
 * <p>Implementations live in the infrastructure layer
 * (e.g. {@code JpaEmployerRepositoryAdapter}).
 */
public interface EmployerWriterRepository {

  /**
   * Persists a new or updated {@link EmployerAggregate}.
   *
   * @param employer the aggregate to save
   * @return the saved aggregate (may include database-generated values)
   */
  EmployerAggregate save(EmployerAggregate employer);

  /**
   * Removes an employer profile from the repository.
   *
   * @param employer the aggregate to delete
   */
  void delete(EmployerAggregate employer);
}