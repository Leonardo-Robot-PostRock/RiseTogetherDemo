package com.ITJobsBackend.authentication.application.ports.in;

import com.ITJobsBackend.authentication.application.usecases.register.RegisterUserCommand;
import com.ITJobsBackend.authentication.application.usecases.register.RegisterUserResponse;

public interface RegisterUserPort {
  RegisterUserResponse execute(RegisterUserCommand command);
}
