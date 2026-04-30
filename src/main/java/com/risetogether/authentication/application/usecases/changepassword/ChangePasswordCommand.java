package com.risetogether.authentication.application.usecases.changepassword;

public record ChangePasswordCommand(String userId, String oldPassword, String newPassword) {}
