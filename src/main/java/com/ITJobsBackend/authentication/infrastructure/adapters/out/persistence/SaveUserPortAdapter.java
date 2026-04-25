package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence;

import org.springframework.stereotype.Component;

import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.repository.UserWriterRepository;

@Component
public class SaveUserPortAdapter implements SaveUserPort {
  private final UserWriterRepository userRepository;

  public SaveUserPortAdapter(UserWriterRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserAggregate save(UserAggregate user) {
    return userRepository.save(user);
  }
}
