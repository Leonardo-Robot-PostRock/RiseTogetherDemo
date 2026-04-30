package com.risetogether.authentication.application.ports.in;

import com.risetogether.authentication.application.usecases.forgot.ForgotPasswordCommand;

public interface ForgotPasswordPort {
  void execute(ForgotPasswordCommand command);
}
