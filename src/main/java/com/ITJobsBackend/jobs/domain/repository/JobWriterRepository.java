package com.ITJobsBackend.jobs.domain.repository;

import com.ITJobsBackend.jobs.domain.aggregate.JobAggregate;

public interface JobWriterRepository {
    JobAggregate save(JobAggregate job);
}
