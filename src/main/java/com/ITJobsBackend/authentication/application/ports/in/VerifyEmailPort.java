package com.ITJobsBackend.authentication.application.ports.in;

import com.ITJobsBackend.authentication.application.usecases.verify.VerifyEmailCommand;

public interface VerifyEmailPort {
  void execute(VerifyEmailCommand command);
}
