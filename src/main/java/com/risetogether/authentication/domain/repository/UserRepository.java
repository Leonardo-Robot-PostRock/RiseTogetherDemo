package com.risetogether.authentication.domain.repository;

/**
 * Composite repository port for {@code UserAggregate} that combines read and write operations.
 *
 * <p>Prefer injecting the more specific {@link UserReaderRepository} or {@link UserWriterRepository}
 * ports in use cases that only need one direction.
 */
public interface UserRepository extends UserReaderRepository, UserWriterRepository {}
