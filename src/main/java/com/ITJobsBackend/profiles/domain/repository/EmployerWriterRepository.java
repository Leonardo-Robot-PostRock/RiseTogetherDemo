package com.ITJobsBackend.profiles.domain.repository;

import com.ITJobsBackend.profiles.domain.aggregate.EmployerAggregate;

public interface EmployerWriterRepository {
  EmployerAggregate save(EmployerAggregate employer);

  void delete(EmployerAggregate employer);
}