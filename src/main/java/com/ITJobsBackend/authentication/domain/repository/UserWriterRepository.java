package com.ITJobsBackend.authentication.domain.repository;

import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.shared.domain.valueobjects.Email;

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

  /**
   * Checks whether a user with the given email already exists.
   *
   * <p>Used by {@code RegisterUserUseCase} to enforce the uniqueness invariant before creating
   * a new user.
   *
   * @param email the email to check
   * @return {@code true} if a user with this email exists in the database
   */
  boolean existsByEmail(Email email);
}
