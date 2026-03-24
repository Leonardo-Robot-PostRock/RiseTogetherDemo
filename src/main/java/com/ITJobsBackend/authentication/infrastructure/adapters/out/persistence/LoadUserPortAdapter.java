package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence;

import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.domain.model.User;
import com.ITJobsBackend.authentication.domain.repository.UserRepository;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoadUserPortAdapter implements LoadUserPort {
    private final UserRepository userRepository;

    public LoadUserPortAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return userRepository.findByEmail(email);
    }
}
