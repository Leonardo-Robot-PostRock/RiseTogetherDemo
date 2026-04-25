package com.ITJobsBackend.authentication.application.ports.out;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import com.ITJobsBackend.authentication.application.query.UserView;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;
/**
 * Read-side (query) output port for user data.
 *
 * <p>Returns lightweight {@link UserView} projections instead of full domain aggregates.
 * Implementations map {@code UserEntity} directly to {@link UserView} — bypassing
 * {@code UserAggregate} reconstitution — and may use optimised SQL projections.
 *
 * <h2>CQRS split</h2>
 * <ul>
 *   <li>{@code QueryUserPort} (this) — returns {@link UserView} — read-only use cases</li>
 *   <li>{@link LoadUserPort} — returns {@code UserAggregate} — mutation use cases</li>
 * </ul>
 */
public interface QueryUserPort {
    Optional<UserView> findByEmail(Email email);
    Optional<UserView> findById(UserId id);
    /** Optimised EXISTS check — does not load the full entity. */
    boolean existsByEmail(Email email);
    /**
     * Returns only the IDs of unverified users created before {@code cutoff}.
     * Fetching only the primary key avoids hydrating the full row for the cleanup batch.
     */
    List<UserId> findUnverifiedUserIdsBefore(Instant cutoff);
}
