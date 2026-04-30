package com.risetogether.authentication.infrastructure.adapters.in.rest.mappers;

import org.mapstruct.Mapper;

import com.risetogether.authentication.application.usecases.changepassword.ChangePasswordCommand;
import com.risetogether.authentication.infrastructure.adapters.in.rest.dto.ChangePasswordRequest;

@Mapper
public interface AccountRestMapper {

    ChangePasswordCommand toCommand(ChangePasswordRequest request);
}

