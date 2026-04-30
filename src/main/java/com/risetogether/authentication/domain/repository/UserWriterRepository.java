package com.risetogether.authentication.domain.repository;

import com.risetogether.authentication.domain.aggregate.UserAggregate;

/**
 * Write-side repository port for {@link UserAggregate}.
 *
 * <p>Implementations live in the infrastructure layer (e.g. {@code JpaUserRepositoryAdapter}).
 */
public interface UserWriterRepository {

  /**
   * Persists a new or updated {@link UserAggregate}.
   *
   * @param user the aggregate to save
   * @return the saved aggregate (may include database-generated values)
   */
  UserAggregate save(UserAggregate user);
}
