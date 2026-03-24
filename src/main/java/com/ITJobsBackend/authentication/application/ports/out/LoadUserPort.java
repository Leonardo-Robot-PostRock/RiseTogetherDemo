package com.ITJobsBackend.authentication.application.ports.out;

import com.ITJobsBackend.authentication.domain.model.User;
import com.ITJobsBackend.shared.domain.valueobjects.Email;

import java.util.Optional;

public interface LoadUserPort {
    Optional<User> findByEmail(Email email);
}
