package com.ITJobsBackend.authentication.infrastructure.adapters.in.rest;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ITJobsBackend.authentication.application.ports.in.ChangePasswordPort;
import com.ITJobsBackend.authentication.application.usecases.changepassword.ChangePasswordCommand;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.ChangePasswordRequest;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {
  private final ChangePasswordPort changePasswordPort;

  public AccountController(ChangePasswordPort changePasswordPort) {
    this.changePasswordPort = changePasswordPort;
  }

  @PostMapping("/change-password")
  public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
    ChangePasswordCommand command = new ChangePasswordCommand(
        request.userId(), request.oldPassword(), request.newPassword());

    changePasswordPort.execute(command);

    return ResponseEntity.ok().build();
  }
}