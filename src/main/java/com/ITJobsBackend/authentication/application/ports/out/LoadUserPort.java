package com.ITJobsBackend.authentication.application.ports.out;

import java.util.Optional;

import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

public interface LoadUserPort {
  Optional<UserAggregate> findByEmail(Email email);

  Optional<UserAggregate> findById(UserId id);
}
