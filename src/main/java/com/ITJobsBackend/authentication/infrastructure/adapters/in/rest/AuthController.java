package com.ITJobsBackend.authentication.infrastructure.adapters.in.rest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ITJobsBackend.authentication.application.ports.in.LoginPort;
import com.ITJobsBackend.authentication.application.ports.in.RegisterUserPort;
import com.ITJobsBackend.authentication.application.usecases.login.AuthTokenResponse;
import com.ITJobsBackend.authentication.application.usecases.login.LoginCommand;
import com.ITJobsBackend.authentication.application.usecases.register.RegisterUserCommand;
import com.ITJobsBackend.authentication.application.usecases.register.RegisterUserResponse;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.AuthResponse;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.LoginRequest;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.RegisterRequest;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.RegisterResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final RegisterUserPort registerUserPort;
  private final LoginPort loginPort;

  public AuthController(RegisterUserPort registerUserPort, LoginPort loginPort) {
    this.registerUserPort = registerUserPort;
    this.loginPort = loginPort;
  }

  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
    RegisterUserCommand command =
        new RegisterUserCommand(request.username(), request.email(), request.password());

    RegisterUserResponse response = registerUserPort.execute(command);

    RegisterResponse dto =
        new RegisterResponse(
            response.userId(), response.username(), response.email(), response.createdAt());

    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    LoginCommand command = new LoginCommand(request.email(), request.password());

    AuthTokenResponse response = loginPort.execute(command);

    AuthResponse dto =
        new AuthResponse(
            response.userId(),
            response.username(),
            response.email(),
            response.accessToken(),
            response.refreshToken());

    return ResponseEntity.ok(dto);
  }
}
