package com.ITJobsBackend.authentication.infrastructure.adapters.in.rest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ITJobsBackend.authentication.application.ports.in.GoogleAuthPort;
import com.ITJobsBackend.authentication.application.ports.in.LoginPort;
import com.ITJobsBackend.authentication.application.ports.in.RegisterUserPort;
import com.ITJobsBackend.authentication.application.ports.in.VerifyEmailPort;
import com.ITJobsBackend.authentication.application.usecases.google.GoogleAuthCommand;
import com.ITJobsBackend.authentication.application.usecases.login.AuthTokenResponse;
import com.ITJobsBackend.authentication.application.usecases.login.LoginCommand;
import com.ITJobsBackend.authentication.application.usecases.register.RegisterUserCommand;
import com.ITJobsBackend.authentication.application.usecases.register.RegisterUserResponse;
import com.ITJobsBackend.authentication.application.usecases.verify.VerifyEmailCommand;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.AuthResponse;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.GoogleLoginRequest;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.LoginRequest;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.RegisterRequest;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.RegisterResponse;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.VerifyEmailRequest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final RegisterUserPort registerUserPort;
  private final LoginPort loginPort;
  private final GoogleAuthPort googleAuthPort;
  private final VerifyEmailPort verifyEmailPort;

  public AuthController(
      RegisterUserPort registerUserPort,
      LoginPort loginPort,
      GoogleAuthPort googleAuthPort,
      VerifyEmailPort verifyEmailPort) {
    this.registerUserPort = registerUserPort;
    this.loginPort = loginPort;
    this.googleAuthPort = googleAuthPort;
    this.verifyEmailPort = verifyEmailPort;
  }

  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
    RegisterUserCommand command =
        new RegisterUserCommand(
            request.username(), request.email(), request.password(), request.termsAccepted());

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

  @PostMapping("/login/google")
  public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
    GoogleAuthCommand command =
        new GoogleAuthCommand(
            request.googleSub(), request.email(), request.name(), request.termsAccepted());

    AuthTokenResponse response = googleAuthPort.execute(command);

    AuthResponse dto =
        new AuthResponse(
            response.userId(),
            response.username(),
            response.email(),
            response.accessToken(),
            response.refreshToken());

    return ResponseEntity.ok(dto);
  }

  @PostMapping("/verify-email")
  public ResponseEntity<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
    VerifyEmailCommand command = new VerifyEmailCommand(request.userId(), request.token());

    verifyEmailPort.execute(command);

    return ResponseEntity.ok().build();
  }
}
