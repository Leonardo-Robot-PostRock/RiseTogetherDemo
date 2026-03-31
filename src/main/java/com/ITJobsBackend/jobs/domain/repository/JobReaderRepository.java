package com.ITJobsBackend.jobs.domain.repository;

import com.ITJobsBackend.jobs.domain.aggregate.JobAggregate;
import com.ITJobsBackend.jobs.domain.specification.JobSpecification;
import com.ITJobsBackend.jobs.domain.valueobjects.JobId;
import java.util.List;
import java.util.Optional;

public interface JobReaderRepository {
  Optional<JobAggregate> findById(JobId id);

  List<JobAggregate> findAll();

  List<JobAggregate> findAll(JobSpecification spec);

  List<JobAggregate> searchByTitle(String title);
}
