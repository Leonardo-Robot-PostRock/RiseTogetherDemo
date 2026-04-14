package com.ITJobsBackend.authentication.application.usecases.changepassword;

public record ChangePasswordCommand(String userId, String oldPassword, String newPassword) {}
