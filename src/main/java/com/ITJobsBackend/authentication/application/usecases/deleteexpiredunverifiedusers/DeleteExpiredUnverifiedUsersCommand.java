package com.ITJobsBackend.authentication.application.usecases.deleteexpiredunverifiedusers;

import java.time.Duration;

public record DeleteExpiredUnverifiedUsersCommand(Duration olderThan) {}