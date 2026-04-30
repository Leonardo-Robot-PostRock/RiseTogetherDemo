package com.risetogether.authentication.infrastructure.adapters.in.rest.mappers;

import org.mapstruct.Mapper;

import com.risetogether.authentication.application.usecases.forgot.ForgotPasswordCommand;
import com.risetogether.authentication.application.usecases.google.GoogleAuthCommand;
import com.risetogether.authentication.application.usecases.login.AuthTokenResponse;
import com.risetogether.authentication.application.usecases.login.LoginCommand;
import com.risetogether.authentication.application.usecases.register.RegisterUserCommand;
import com.risetogether.authentication.application.usecases.register.RegisterUserResponse;
import com.risetogether.authentication.application.usecases.verify.VerifyEmailCommand;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.AuthResponse;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.ForgotPasswordRequest;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.GoogleLoginRequest;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.LoginRequest;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.RegisterRequest;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.RegisterResponse;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.VerifyEmailRequest;

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

