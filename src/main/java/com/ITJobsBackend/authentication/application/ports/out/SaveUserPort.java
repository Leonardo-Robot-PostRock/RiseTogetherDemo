package com.ITJobsBackend.authentication.application.ports.out;

import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;

public interface SaveUserPort {
  UserAggregate save(UserAggregate user);
}
