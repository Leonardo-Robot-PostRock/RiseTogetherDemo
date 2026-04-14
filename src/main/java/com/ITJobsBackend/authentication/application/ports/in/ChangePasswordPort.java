package com.ITJobsBackend.authentication.application.ports.in;

import com.ITJobsBackend.authentication.application.usecases.changepassword.ChangePasswordCommand;

public interface ChangePasswordPort {
  void execute(ChangePasswordCommand command);
}