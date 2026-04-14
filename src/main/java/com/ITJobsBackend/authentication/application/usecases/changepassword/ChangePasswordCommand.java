package com.ITJobsBackend.authentication.application.usecases.changepassword;

import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;

public record ChangePasswordCommand(String userId, String oldPassword, String newPassword) {}