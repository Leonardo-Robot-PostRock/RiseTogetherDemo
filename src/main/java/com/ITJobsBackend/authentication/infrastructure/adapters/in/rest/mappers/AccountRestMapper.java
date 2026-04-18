package com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.mappers;

import org.mapstruct.Mapper;

import com.ITJobsBackend.authentication.application.usecases.changepassword.ChangePasswordCommand;
import com.ITJobsBackend.authentication.infrastructure.adapters.in.rest.dto.ChangePasswordRequest;

@Mapper
public interface AccountRestMapper {

    ChangePasswordCommand toCommand(ChangePasswordRequest request);
}

