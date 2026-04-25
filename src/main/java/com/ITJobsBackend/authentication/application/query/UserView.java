package com.ITJobsBackend.authentication.application.query;

import java.util.List;

import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

/**
 * Read model (projection) for the {@code authentication} bounded context query side.
 *
 * <p>Used by <em>read-only</em> use cases (login, token refresh, forgot password) so that the full
 * {@link com.ITJobsBackend.authentication.domain.aggregate.UserAggregate} write model — with its
 * invariant checks, event list, and domain behaviour — is never instantiated for pure query
 * operations.
 *
 * <p>The query-side adapter maps a {@code UserEntity} directly to this projection, which may also
 * be backed by an optimised SQL {@code SELECT} that fetches only the required columns.
 *
 * <h2>CQRS contract</h2>
 *
 * <ul>
 *   <li><b>Write side</b>: load via {@code LoadUserPort} → returns {@code UserAggregate} → mutate →
 *       save (command flow).
 *   <li><b>Read side</b>: load via {@code QueryUserPort} → returns {@code UserView} → read-only
 *       (query flow).
 * </ul>
 */
public record UserView(
    UserId id,
    String username,
    String email,
    HashedPassword hashedPassword,
    boolean active,
    boolean emailVerified,
    List<String> roles) {}
