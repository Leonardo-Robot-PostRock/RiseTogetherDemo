package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.repository.UserReaderRepository;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

/**
 * Command-side adapter that implements {@link LoadUserPort}.
 *
 * <p>Delegates to {@link UserReaderRepository} to rehydrate the {@code UserAggregate} write model
 * by natural key. Used exclusively by mutation use cases (VerifyEmail, ChangePassword, GoogleAuth,
 * ResendVerification) that load an aggregate, call domain behaviour, and then save it.
 *
 * <p>Read-only operations (login, token refresh, existence checks, cleanup batch) are handled by
 * {@link QueryUserPortAdapter} via the {@code QueryUserPort}.
 */
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
