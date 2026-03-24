package com.ITJobsBackend.jobs.domain.repository;

import com.ITJobsBackend.jobs.domain.model.Job;
import com.ITJobsBackend.jobs.domain.valueobjects.JobId;

import java.util.List;
import java.util.Optional;

public interface JobRepository {
    Job save(Job job);
    Optional<Job> findById(JobId id);
    List<Job> findAll();
    List<Job> searchByTitle(String title);
    void delete(JobId id);
}
