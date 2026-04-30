package com.risetogether.jobs.infrastructure.adapters.in.rest.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record AddSkillsRequest(@NotEmpty List<String> skills) {}