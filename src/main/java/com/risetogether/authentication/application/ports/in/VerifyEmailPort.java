package com.risetogether.authentication.application.ports.in;

import com.risetogether.authentication.application.usecases.verify.VerifyEmailCommand;

public interface VerifyEmailPort {
  void execute(VerifyEmailCommand command);
}
