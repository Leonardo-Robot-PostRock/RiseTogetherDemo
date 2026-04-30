package com.risetogether.authentication.application.ports.out;

import java.util.Optional;

import com.risetogether.authentication.domain.aggregate.UserAggregate;
import com.risetogether.shared.domain.valueobjects.Email;
import com.risetogether.shared.domain.valueobjects.UserId;

/**
 * Write-side (command) output port for loading a {@link UserAggregate}.
 *
 * <p>Used exclusively by use cases that <em>mutate</em> the aggregate after loading it:
 * {@code VerifyEmailUseCase}, {@code ChangePasswordUseCase}, {@code GoogleAuthUseCase},
 * {@code ResendVerificationUseCase}.
 *
 * <p>Read-only use cases (login, token refresh, etc.) use {@link QueryUserPort} instead,
 * which returns a lightweight {@code UserView} projection without instantiating the aggregate.
 *
 * <h2>CQRS split</h2>
 * <ul>
 *   <li>{@code LoadUserPort} (this) — returns {@link UserAggregate} — mutation use cases</li>
 *   <li>{@link QueryUserPort} — returns {@code UserView} — read-only use cases</li>
 * </ul>
 */
public interface LoadUserPort {

    /**
     * @param email the user's email address (normalised, lower-case)
     * @return the matching aggregate, or {@link Optional#empty()} if not found
     */
    Optional<UserAggregate> findByEmail(Email email);

    /**
     * @param id the user's unique identifier
     * @return the matching aggregate, or {@link Optional#empty()} if not found
     */
    Optional<UserAggregate> findById(UserId id);
}
