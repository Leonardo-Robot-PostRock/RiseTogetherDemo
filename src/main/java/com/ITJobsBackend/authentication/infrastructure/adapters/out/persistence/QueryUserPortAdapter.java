package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ITJobsBackend.authentication.application.ports.out.QueryUserPort;
import com.ITJobsBackend.authentication.application.query.UserView;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.SpringDataJpaUserRepository;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.mappers.read.UserViewMapper;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

/**
 * Read-side adapter that implements {@link QueryUserPort}.
 *
 * <p>Maps {@link com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.entities.UserEntity}
 * directly to {@link UserView} via {@link UserViewMapper#toView}, bypassing {@code UserAggregate}
 * reconstitution: no invariant checks, no domain event list, no aggregate behaviour is created.
 *
 * <p>For the cleanup batch query only the {@code id} column is projected
 * ({@code SELECT u.id ...}), avoiding a full entity hydration.
 */
@Component
public class QueryUserPortAdapter implements QueryUserPort {

    private final SpringDataJpaUserRepository jpaRepository;
    private final UserViewMapper userViewMapper;

    public QueryUserPortAdapter(
            SpringDataJpaUserRepository jpaRepository, UserViewMapper userViewMapper) {
        this.jpaRepository = jpaRepository;
        this.userViewMapper = userViewMapper;
    }

    // ── Read-model queries ────────────────────────────────────────────────────

    @Override
    public Optional<UserView> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value()).map(userViewMapper::toView);
    }

    @Override
    public Optional<UserView> findById(UserId id) {
        return jpaRepository.findById(id.value()).map(userViewMapper::toView);
    }

    /** Delegates to the database-level {@code EXISTS} query — no entity hydration. */
    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }

    /** Returns only IDs — avoids full entity hydration for the deletion batch. */
    @Override
    public List<UserId> findUnverifiedUserIdsBefore(Instant cutoff) {
        return jpaRepository.findUnverifiedUserIdsBefore(cutoff).stream()
            .map(UserId::of)
            .toList();
    }
}
