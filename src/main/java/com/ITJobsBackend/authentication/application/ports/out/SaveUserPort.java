package com.ITJobsBackend.authentication.application.ports.out;

import com.ITJobsBackend.authentication.domain.model.User;
import com.ITJobsBackend.shared.domain.valueobjects.Email;

public interface SaveUserPort {
    User save(User user);
    boolean existsByEmail(Email email);
}
