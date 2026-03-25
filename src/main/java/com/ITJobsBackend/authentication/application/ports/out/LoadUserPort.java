package com.ITJobsBackend.authentication.application.ports.out;

import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.shared.domain.valueobjects.Email;

import java.util.Optional;

public interface LoadUserPort {
    Optional<UserAggregate> findByEmail(Email email);
}
