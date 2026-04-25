package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;

public interface SpringDataJpaUserRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByEmail(String email);

  Optional<UserEntity> findByUsername(String username);

  boolean existsByEmail(String email);

  @Query("SELECT u FROM UserEntity u WHERE u.emailVerified = false AND u.createdAt < :cutoff")
  List<UserEntity> findUnverifiedUsersOlderThan(@Param("cutoff") Instant cutoff);

  /** Optimised ID-only projection for the cleanup batch job. */
  @Query("SELECT u.id FROM UserEntity u WHERE u.emailVerified = false AND u.createdAt < :cutoff")
  List<UUID> findUnverifiedUserIdsBefore(@Param("cutoff") Instant cutoff);
}
