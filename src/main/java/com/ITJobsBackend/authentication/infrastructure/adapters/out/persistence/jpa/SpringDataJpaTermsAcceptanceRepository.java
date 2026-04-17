package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.entities.TermsAcceptanceEntity;

public interface SpringDataJpaTermsAcceptanceRepository
    extends JpaRepository<TermsAcceptanceEntity, UUID> {}
