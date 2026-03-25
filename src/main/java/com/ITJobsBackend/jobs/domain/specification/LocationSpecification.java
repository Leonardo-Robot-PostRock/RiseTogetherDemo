package com.ITJobsBackend.jobs.domain.specification;

import com.ITJobsBackend.jobs.domain.aggregate.JobAggregate;

public class LocationSpecification implements JobSpecification {
    private final String location;

    public LocationSpecification(String location) {
        this.location = location.toLowerCase();
    }

    @Override
    public boolean isSatisfiedBy(JobAggregate job) {
        return job.getLocation() != null &&
               job.getLocation().toLowerCase().contains(location);
    }
}
