package com.ITJobsBackend.jobs.domain.repository;

import java.util.List;
import java.util.Optional;

import com.ITJobsBackend.jobs.domain.aggregate.JobAggregate;
import com.ITJobsBackend.jobs.domain.specification.JobSpecification;
import com.ITJobsBackend.jobs.domain.valueobjects.JobId;

/**
 * Read-side repository port for {@link JobAggregate}.
 *
 * <p>Implementations live in the infrastructure layer (e.g. {@code JpaJobRepositoryAdapter}).
 */
public interface JobReaderRepository {

  /**
   * @param id the job's unique identifier
   * @return the matching aggregate, or {@link Optional#empty()} if not found
   */
  Optional<JobAggregate> findById(JobId id);

  /**
   * @return all job aggregates in the repository (no filtering)
   */
  List<JobAggregate> findAll();

  /**
   * Returns all jobs that satisfy the given specification.
   *
   * @param spec the specification to evaluate against each job
   * @return matching job aggregates; empty list if none match
   */
  List<JobAggregate> findAll(JobSpecification spec);

  /**
   * Returns all jobs whose title contains the given keyword (case-insensitive).
   *
   * @param title the keyword to search for
   * @return matching job aggregates
   */
  List<JobAggregate> searchByTitle(String title);
}
