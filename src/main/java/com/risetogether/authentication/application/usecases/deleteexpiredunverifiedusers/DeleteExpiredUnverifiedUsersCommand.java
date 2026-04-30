package com.risetogether.authentication.application.usecases.deleteexpiredunverifiedusers;

import java.time.Duration;

public record DeleteExpiredUnverifiedUsersCommand(Duration olderThan) {}