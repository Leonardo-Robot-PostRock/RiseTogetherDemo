package com.risetogether.authentication.domain.repository;

import java.util.Optional;

import com.risetogether.authentication.domain.aggregate.UserAggregate;
import com.risetogether.authentication.domain.valueobjects.Username;
import com.risetogether.shared.domain.valueobjects.Email;
import com.risetogether.shared.domain.valueobjects.UserId;

/**
 * Read-side repository port for {@link UserAggregate}.
 *
 * <p>Exposes only the queries the domain needs to rehydrate aggregates by their natural keys.
 * Application- or infrastructure-specific queries (e.g. existence checks, scheduled cleanup)
 * belong in the application-layer ports ({@code LoadUserPort}) and their adapters.
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
