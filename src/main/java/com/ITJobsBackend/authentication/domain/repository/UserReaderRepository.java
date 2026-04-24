package com.ITJobsBackend.authentication.domain.repository;

import java.time.Instant;
import java.util.List;
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

  /**
   * Finds all unverified users whose account was created before the given cutoff time.
   *
   * @param cutoff the instant before which the user must have been created
   * @return list of unverified users created before the cutoff
   */
  List<UserAggregate> findUnverifiedUsersOlderThan(Instant cutoff);
}
