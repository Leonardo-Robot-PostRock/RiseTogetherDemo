package com.ITJobsBackend.authentication.application.ports.in;

import com.ITJobsBackend.authentication.application.usecases.resendverification.ResendVerificationCommand;

public interface ResendVerificationPort {
    void execute(ResendVerificationCommand command);
}