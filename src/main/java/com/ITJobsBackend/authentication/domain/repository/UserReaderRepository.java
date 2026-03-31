package com.ITJobsBackend.authentication.domain.repository;

import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;
import java.util.Optional;

public interface UserReaderRepository {
  Optional<UserAggregate> findById(UserId id);

  Optional<UserAggregate> findByEmail(Email email);

  Optional<UserAggregate> findByUsername(Username username);
}
