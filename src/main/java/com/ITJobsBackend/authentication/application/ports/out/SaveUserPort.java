package com.ITJobsBackend.authentication.application.ports.out;

import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;

import com.ITJobsBackend.shared.domain.valueobjects.Email;

public interface SaveUserPort {
  UserAggregate save(UserAggregate user);

  boolean existsByEmail(Email email);
}
