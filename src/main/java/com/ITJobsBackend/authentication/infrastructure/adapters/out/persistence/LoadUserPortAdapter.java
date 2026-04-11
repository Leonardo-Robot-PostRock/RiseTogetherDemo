package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.repository.UserReaderRepository;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@Component
public class LoadUserPortAdapter implements LoadUserPort {
  private final UserReaderRepository userRepository;

  public LoadUserPortAdapter(UserReaderRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Optional<UserAggregate> findByEmail(Email email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public Optional<UserAggregate> findById(UserId id) {
    return userRepository.findById(id);
  }
}
