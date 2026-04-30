package com.risetogether.authentication.infrastructure.adapters.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.risetogether.authentication.application.ports.out.LoadUserPort;
import com.risetogether.authentication.domain.aggregate.UserAggregate;
import com.risetogether.authentication.domain.repository.UserReaderRepository;
import com.risetogether.shared.domain.valueobjects.Email;
import com.risetogether.shared.domain.valueobjects.UserId;

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
