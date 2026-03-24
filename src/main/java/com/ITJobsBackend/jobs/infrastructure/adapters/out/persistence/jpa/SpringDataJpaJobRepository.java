package com.ITJobsBackend.jobs.infrastructure.adapters.out.persistence.jpa;

import com.ITJobsBackend.jobs.infrastructure.adapters.out.persistence.jpa.entities.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

interface SpringDataJpaJobRepository extends JpaRepository<JobEntity, UUID> {
    @Query("SELECT j FROM JobEntity j WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<JobEntity> searchByTitle(@Param("title") String title);
}
