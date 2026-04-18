package com.ITJobsBackend.authentication.infrastructure.adapters.in.rest;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ITJobsBackend.authentication.application.ports.in.ChangePasswordPort;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.ChangePasswordRequest;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.mappers.AccountRestMapper;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {
  private final ChangePasswordPort changePasswordPort;
  private final AccountRestMapper accountRestMapper;

  public AccountController(ChangePasswordPort changePasswordPort, AccountRestMapper accountRestMapper) {
    this.changePasswordPort = changePasswordPort;
    this.accountRestMapper = accountRestMapper;
  }

  @PostMapping("/change-password")
  public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
    changePasswordPort.execute(accountRestMapper.toCommand(request));

    return ResponseEntity.ok().build();
  }
}
