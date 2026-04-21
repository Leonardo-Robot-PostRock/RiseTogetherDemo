package com.ITJobsBackend.jobs.infrastructure.adapters.in.rest.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.ITJobsBackend.jobs.application.usecases.addskills.AddSkillsCommand;
import com.ITJobsBackend.jobs.application.usecases.createjob.CreateJobCommand;
import com.ITJobsBackend.jobs.application.usecases.searchjobs.SearchJobsQuery;
import com.ITJobsBackend.jobs.infrastructure.adapters.in.rest.dto.AddSkillsRequest;
import com.ITJobsBackend.jobs.infrastructure.adapters.in.rest.dto.CreateJobRequest;

@Mapper
public interface JobRestMapper {

    CreateJobCommand toCommand(CreateJobRequest request);

    AddSkillsCommand toCommand(AddSkillsRequest request, String jobId);

    default SearchJobsQuery toQuery(String title) {
        return new SearchJobsQuery(title);
    }
}

