package com.ITJobsBackend.profiles.infrastructure.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ITJobsBackend.profiles.infrastructure.adapters.out.persistence.jpa.entities.EmployerEntity;

public interface SpringDataJpaEmployerRepository extends JpaRepository<EmployerEntity, UUID> {
  Optional<EmployerEntity> findByCompanyName(String companyName);

  @Query("SELECT e FROM EmployerEntity e WHERE e.id = :userId")
  Optional<EmployerEntity> findByUserId(@Param("userId") UUID userId);

  boolean existsByCompanyName(String companyName);

  boolean existsById(UUID id);
}