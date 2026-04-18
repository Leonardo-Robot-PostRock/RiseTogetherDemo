package com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.mappers;

import org.mapstruct.Mapper;

import com.ITJobsBackend.authentication.application.usecases.forgot.ForgotPasswordCommand;
import com.ITJobsBackend.authentication.application.usecases.google.GoogleAuthCommand;
import com.ITJobsBackend.authentication.application.usecases.login.AuthTokenResponse;
import com.ITJobsBackend.authentication.application.usecases.login.LoginCommand;
import com.ITJobsBackend.authentication.application.usecases.register.RegisterUserCommand;
import com.ITJobsBackend.authentication.application.usecases.register.RegisterUserResponse;
import com.ITJobsBackend.authentication.application.usecases.verify.VerifyEmailCommand;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.AuthResponse;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.ForgotPasswordRequest;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.GoogleLoginRequest;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.LoginRequest;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.RegisterRequest;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.RegisterResponse;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.VerifyEmailRequest;

@Mapper
public interface AuthRestMapper {

    RegisterUserCommand toCommand(RegisterRequest request);

    RegisterResponse toDto(RegisterUserResponse response);

    LoginCommand toCommand(LoginRequest request);

    GoogleAuthCommand toCommand(GoogleLoginRequest request);

    VerifyEmailCommand toCommand(VerifyEmailRequest request);

    ForgotPasswordCommand toCommand(ForgotPasswordRequest request);

    AuthResponse toDto(AuthTokenResponse response);
}

