package com.risetogether.jobs.domain.repository;

/**
 * Composite repository port for {@code JobAggregate} that combines read and write operations.
 *
 * <p>Prefer injecting the more specific {@link JobReaderRepository} or {@link JobWriterRepository}
 * ports in use cases that only need one direction.
 */
public interface JobRepository extends JobReaderRepository, JobWriterRepository {}
