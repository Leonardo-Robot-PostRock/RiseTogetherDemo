package com.ITJobsBackend.jobs.infrastructure.adapters.out.persistence.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ITJobsBackend.jobs.infrastructure.adapters.out.persistence.jpa.entities.JobEntity;

interface SpringDataJpaJobRepository extends JpaRepository<JobEntity, UUID> {
  @Query("SELECT j FROM JobEntity j WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))")
  List<JobEntity> searchByTitle(@Param("title") String title);
}
