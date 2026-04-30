package com.risetogether.authentication.application.ports.in;

import com.risetogether.authentication.application.usecases.changepassword.ChangePasswordCommand;

public interface ChangePasswordPort {
  void execute(ChangePasswordCommand command);
}
