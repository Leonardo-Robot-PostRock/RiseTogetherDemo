package com.ITJobsBackend.jobs.domain.specification;

import com.ITJobsBackend.jobs.domain.aggregate.JobAggregate;

public class CompanySpecification implements JobSpecification {
    private final String company;

    public CompanySpecification(String company) {
        this.company = company;
    }

    @Override
    public boolean isSatisfiedBy(JobAggregate job) {
        return job.getCompany().equalsIgnoreCase(company);
    }
}
