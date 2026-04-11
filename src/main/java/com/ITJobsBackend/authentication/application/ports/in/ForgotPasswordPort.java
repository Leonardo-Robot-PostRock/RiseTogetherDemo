package com.ITJobsBackend.authentication.application.ports.in;

import com.ITJobsBackend.authentication.application.usecases.forgot.ForgotPasswordCommand;

public interface ForgotPasswordPort {
  void execute(ForgotPasswordCommand command);
}