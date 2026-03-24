package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence;

import com.ITJobsBackend.authentication.application.ports.out.SaveUserPort;
import com.ITJobsBackend.authentication.domain.model.User;
import com.ITJobsBackend.authentication.domain.repository.UserRepository;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import org.springframework.stereotype.Component;

@Component
public class SaveUserPortAdapter implements SaveUserPort {
    private final UserRepository userRepository;

    public SaveUserPortAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userRepository.existsByEmail(email);
    }
}
