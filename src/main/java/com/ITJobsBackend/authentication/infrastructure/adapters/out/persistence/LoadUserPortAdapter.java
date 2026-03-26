package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence;

import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.repository.UserReaderRepository;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
}
