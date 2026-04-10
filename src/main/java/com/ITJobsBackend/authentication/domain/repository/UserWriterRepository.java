package com.ITJobsBackend.authentication.domain.repository;

import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.shared.domain.valueobjects.Email;

public interface UserWriterRepository {
  UserAggregate save(UserAggregate user);

  boolean existsByEmail(Email email);
}
