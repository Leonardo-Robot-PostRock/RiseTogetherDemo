package com.ITJobsBackend.jobs.application.ports.in;

import java.util.List;

import com.ITJobsBackend.jobs.application.usecases.addskills.AddSkillsCommand;
import com.ITJobsBackend.jobs.application.usecases.searchjobs.JobResponse;

public interface AddSkillsPort {
    JobResponse execute(AddSkillsCommand command);
}