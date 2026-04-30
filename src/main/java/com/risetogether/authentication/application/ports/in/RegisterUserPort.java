package com.risetogether.authentication.application.ports.in;

import com.risetogether.authentication.application.usecases.register.RegisterUserCommand;
import com.risetogether.authentication.application.usecases.register.RegisterUserResponse;

public interface RegisterUserPort {
  RegisterUserResponse execute(RegisterUserCommand command);
}
