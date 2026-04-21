package com.ITJobsBackend.jobs.domain.specification;

import com.ITJobsBackend.jobs.domain.aggregate.JobAggregate;
import com.ITJobsBackend.shared.domain.specification.Specification;

/**
 * Typed {@link Specification} for filtering {@link JobAggregate} instances.
 *
 * <p>Implementations encapsulate a single filtering criterion. Compose them via the inherited
 * {@link #and(Specification)} and {@link #or(Specification)} default methods.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * JobSpecification spec = new TitleContainsSpecification("java")
 *     .and(new JobStatusSpecification(JobStatus.OPEN));
 * List<JobAggregate> results = jobRepo.findAll(spec);
 * }</pre>
 */
@FunctionalInterface
public interface JobSpecification extends Specification<JobAggregate> {}
