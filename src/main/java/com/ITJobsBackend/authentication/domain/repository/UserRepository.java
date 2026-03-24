package com.ITJobsBackend.authentication.domain.repository;

import com.ITJobsBackend.authentication.domain.model.User;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(Email email);
    Optional<User> findByUsername(Username username);
    boolean existsByEmail(Email email);
    void delete(UserId id);
}
