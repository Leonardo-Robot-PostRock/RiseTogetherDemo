package com.risetogether.authentication.application.ports.in;

import com.risetogether.authentication.application.usecases.resendverification.ResendVerificationCommand;

public interface ResendVerificationPort {
    void execute(ResendVerificationCommand command);
}