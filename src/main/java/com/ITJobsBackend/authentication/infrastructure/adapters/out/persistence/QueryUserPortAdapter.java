package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ITJobsBackend.authentication.application.ports.out.QueryUserPort;
import com.ITJobsBackend.authentication.application.query.UserView;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.SpringDataJpaUserRepository;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

/**
 * Read-side adapter that implements {@link QueryUserPort}.
 *
 * <p>Maps {@link UserEntity} directly to {@link UserView}, bypassing {@code UserAggregate}
 * reconstitution: no invariant checks, no domain event list, no aggregate behaviour is created.
 *
 * <p>For the cleanup batch query only the {@code id} column is projected
 * ({@code SELECT u.id ...}), avoiding a full entity hydration.
 */
@Component
public class QueryUserPortAdapter implements QueryUserPort {

    private final SpringDataJpaUserRepository jpaRepository;

    public QueryUserPortAdapter(SpringDataJpaUserRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    // ── Read-model queries ────────────────────────────────────────────────────

    @Override
    public Optional<UserView> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value()).map(this::toView);
    }

    @Override
    public Optional<UserView> findById(UserId id) {
        return jpaRepository.findById(id.value()).map(this::toView);
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

    // ── Private mapping ───────────────────────────────────────────────────────

    /**
     * Maps a {@link UserEntity} to a {@link UserView} read model without instantiating
     * the full {@code UserAggregate} write model.
     */
    private UserView toView(UserEntity entity) {
        return new UserView(
            UserId.of(entity.getId()),
            entity.getUsername(),
            entity.getEmail(),
            HashedPassword.fromHash(entity.getPassword()),
            entity.isActive(),
            entity.isEmailVerified(),
            List.copyOf(entity.getRoles()));
    }
}

