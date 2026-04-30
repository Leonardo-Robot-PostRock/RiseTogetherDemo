package com.risetogether.authentication.infrastructure.adapters.out.persistence;

import org.springframework.stereotype.Component;

import com.risetogether.authentication.application.ports.out.DeleteUserPort;
import com.risetogether.authentication.infrastructure.adapters.out.persistence.jpa.SpringDataJpaUserRepository;
import com.risetogether.shared.domain.valueobjects.UserId;

/**
 * Adapter that implements {@link DeleteUserPort} by delegating directly to
 * {@link SpringDataJpaUserRepository#deleteById}. Deletion is a persistence operation —
 * the domain model does not define a {@code delete} method on the aggregate — so this adapter
 * correctly bypasses the domain repository layer.
 */
@Component
public class DeleteUserPortAdapter implements DeleteUserPort {
    private final SpringDataJpaUserRepository jpaRepository;

    public DeleteUserPortAdapter(SpringDataJpaUserRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void delete(UserId id) {
        jpaRepository.deleteById(id.value());
    }
}
