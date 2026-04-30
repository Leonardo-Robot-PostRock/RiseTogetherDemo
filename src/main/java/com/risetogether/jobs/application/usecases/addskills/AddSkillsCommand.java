package com.risetogether.jobs.application.usecases.addskills;

import java.util.List;

public record AddSkillsCommand(String jobId, List<String> skills) {}