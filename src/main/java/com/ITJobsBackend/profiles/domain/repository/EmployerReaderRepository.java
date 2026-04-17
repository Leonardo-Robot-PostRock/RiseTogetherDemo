package com.ITJobsBackend.profiles.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.ITJobsBackend.profiles.domain.aggregate.EmployerAggregate;
import com.ITJobsBackend.shared.domain.valueobjects.EmployerId;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

public interface EmployerReaderRepository {
  Optional<EmployerAggregate> findById(EmployerId id);

  Optional<EmployerAggregate> findByUserId(UserId userId);

  Optional<EmployerAggregate> findById(UUID id);

  boolean existsByUserId(UserId userId);

  boolean existsByCompanyName(String companyName);
}