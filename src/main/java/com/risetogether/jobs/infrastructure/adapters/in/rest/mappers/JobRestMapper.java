package com.risetogether.jobs.infrastructure.adapters.in.rest.mappers;

import org.mapstruct.Mapper;

import com.risetogether.jobs.application.usecases.addskills.AddSkillsCommand;
import com.risetogether.jobs.application.usecases.createjob.CreateJobCommand;
import com.risetogether.jobs.application.usecases.searchjobs.SearchJobsQuery;
import com.risetogether.jobs.infrastructure.adapters.in.rest.dto.AddSkillsRequest;
import com.risetogether.jobs.infrastructure.adapters.in.rest.dto.CreateJobRequest;

@Mapper
public interface JobRestMapper {

    CreateJobCommand toCommand(CreateJobRequest request);

    AddSkillsCommand toCommand(AddSkillsRequest request, String jobId);

    default SearchJobsQuery toQuery(String title) {
        return new SearchJobsQuery(title);
    }
}

