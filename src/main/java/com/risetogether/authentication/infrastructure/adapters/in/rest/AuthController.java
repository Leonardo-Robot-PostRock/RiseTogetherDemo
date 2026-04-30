package com.risetogether.authentication.infrastructure.adapters.in.rest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.risetogether.authentication.application.ports.in.ForgotPasswordPort;
import com.risetogether.authentication.application.ports.in.GoogleAuthPort;
import com.risetogether.authentication.application.ports.in.LoginPort;
import com.risetogether.authentication.application.ports.in.RefreshTokenPort;
import com.risetogether.authentication.application.ports.in.RegisterUserPort;
import com.risetogether.authentication.application.ports.in.ResendVerificationPort;
import com.risetogether.authentication.application.ports.in.VerifyEmailPort;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.AuthResponse;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.ForgotPasswordRequest;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.GoogleLoginRequest;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.LoginRequest;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.RefreshTokenRequest;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.RegisterRequest;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.ResendVerificationRequest;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.RegisterResponse;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.ResendVerificationRequest;
import com.risetogether.authentication.application.usecases.resendverification.ResendVerificationCommand;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.VerifyEmailRequest;
import com.risetogether.authentication.infrastructure.adapters.in.rest.mappers.AuthRestMapper;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final RegisterUserPort registerUserPort;
    private final LoginPort loginPort;
    private final GoogleAuthPort googleAuthPort;
    private final VerifyEmailPort verifyEmailPort;
    private final RefreshTokenPort refreshTokenPort;
    private final ForgotPasswordPort forgotPasswordPort;
    private final ResendVerificationPort resendVerificationPort;
    private final AuthRestMapper authRestMapper;

    public AuthController(
            RegisterUserPort registerUserPort,
            LoginPort loginPort,
            GoogleAuthPort googleAuthPort,
            VerifyEmailPort verifyEmailPort,
            RefreshTokenPort refreshTokenPort,
            ForgotPasswordPort forgotPasswordPort,
            ResendVerificationPort resendVerificationPort,
            AuthRestMapper authRestMapper) {
        this.registerUserPort = registerUserPort;
        this.loginPort = loginPort;
        this.googleAuthPort = googleAuthPort;
        this.verifyEmailPort = verifyEmailPort;
        this.refreshTokenPort = refreshTokenPort;
        this.forgotPasswordPort = forgotPasswordPort;
        this.resendVerificationPort = resendVerificationPort;
        this.authRestMapper = authRestMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse dto =
                authRestMapper.toDto(registerUserPort.execute(authRestMapper.toCommand(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse dto = authRestMapper.toDto(loginPort.execute(authRestMapper.toCommand(request)));
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login/google")
    public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        AuthResponse dto =
                authRestMapper.toDto(googleAuthPort.execute(authRestMapper.toCommand(request)));
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        verifyEmailPort.execute(authRestMapper.toCommand(request));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse dto = authRestMapper.toDto(refreshTokenPort.execute(request.refreshToken()));
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        forgotPasswordPort.execute(authRestMapper.toCommand(request));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        resendVerificationPort.execute(new ResendVerificationCommand(request.email()));
        return ResponseEntity.ok().build();
    }
}
