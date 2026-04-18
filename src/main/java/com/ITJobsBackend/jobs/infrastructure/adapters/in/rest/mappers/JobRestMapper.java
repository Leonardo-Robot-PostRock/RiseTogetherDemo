package com.ITJobsBackend.jobs.infrastructure.adapters.in.rest.mappers;

import org.mapstruct.Mapper;

import com.ITJobsBackend.jobs.application.usecases.createjob.CreateJobCommand;
import com.ITJobsBackend.jobs.application.usecases.searchjobs.SearchJobsQuery;
import com.ITJobsBackend.jobs.infrastructure.adapters.in.rest.dto.CreateJobRequest;

@Mapper
public interface JobRestMapper {

    CreateJobCommand toCommand(CreateJobRequest request);

    default SearchJobsQuery toQuery(String title) {
        return new SearchJobsQuery(title);
    }
}

