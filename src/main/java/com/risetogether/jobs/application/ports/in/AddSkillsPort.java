package com.risetogether.jobs.application.ports.in;

import com.risetogether.jobs.application.usecases.addskills.AddSkillsCommand;
import com.risetogether.jobs.application.usecases.searchjobs.JobResponse;

public interface AddSkillsPort {
    JobResponse execute(AddSkillsCommand command);
}