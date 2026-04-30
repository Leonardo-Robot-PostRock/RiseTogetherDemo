package com.risetogether.profiles.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.risetogether.profiles.domain.aggregate.EmployerAggregate;
import com.risetogether.shared.domain.valueobjects.EmployerId;
import com.risetogether.shared.domain.valueobjects.UserId;

/**
 * Read-side repository port for {@link EmployerAggregate}.
 *
 * <p>Implementations live in the infrastructure layer
 * (e.g. {@code JpaEmployerRepositoryAdapter}).
 */
public interface EmployerReaderRepository {

  /**
   * @param id the employer's unique identifier
   * @return the matching aggregate, or {@link Optional#empty()} if not found
   */
  Optional<EmployerAggregate> findById(EmployerId id);

  /**
   * Looks up an employer by the platform {@link UserId} that owns it.
   *
   * @param userId the owning user's identifier
   * @return the matching aggregate, or {@link Optional#empty()} if not found
   */
  Optional<EmployerAggregate> findByUserId(UserId userId);

  /**
   * Convenience overload that accepts a raw {@link UUID} instead of a typed {@link EmployerId}.
   *
   * @param id the raw UUID of the employer
   * @return the matching aggregate, or {@link Optional#empty()} if not found
   */
  Optional<EmployerAggregate> findById(UUID id);

  /**
   * @param userId the user id to check
   * @return {@code true} if an employer profile linked to this user already exists
   */
  boolean existsByUserId(UserId userId);

  /**
   * @param companyName the company name to check (exact match)
   * @return {@code true} if a company with this name already exists
   */
  boolean existsByCompanyName(String companyName);
}