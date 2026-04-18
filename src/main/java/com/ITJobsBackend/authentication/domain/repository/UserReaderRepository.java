package com.ITJobsBackend.authentication.domain.repository;

import java.util.Optional;

import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

/**
 * Read-side repository port for {@link UserAggregate}.
 *
 * <p>Implementations live in the infrastructure layer (e.g. {@code JpaUserRepositoryAdapter}).
 */
public interface UserReaderRepository {

  /**
   * @param id the user's unique identifier
   * @return the matching aggregate, or {@link Optional#empty()} if not found
   */
  Optional<UserAggregate> findById(UserId id);

  /**
   * @param email the user's email address (normalised, lower-case)
   * @return the matching aggregate, or {@link Optional#empty()} if not found
   */
  Optional<UserAggregate> findByEmail(Email email);

  /**
   * @param username the user's display name
   * @return the matching aggregate, or {@link Optional#empty()} if not found
   */
  Optional<UserAggregate> findByUsername(Username username);
}
