package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.mappers;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.valueobjects.GoogleSub;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@Component
public class UserMapper {

  public UserEntity toEntity(UserAggregate domain) {
    UserEntity entity = new UserEntity();
    entity.setId(domain.getId().value());
    entity.setUsername(domain.getUsername().value());
    entity.setEmail(domain.getEmail().value());
    entity.setPassword(domain.getPassword().value());
    entity.setActive(domain.isActive());
    entity.setEmailVerified(domain.isEmailVerified());
    entity.setCreatedAt(domain.getCreatedAt().value());
    entity.setUpdatedAt(domain.getUpdatedAt().value());
    entity.setRoles(new ArrayList<>(domain.getRoles()));
    entity.setGoogleSub(domain.getGoogleSub() != null ? domain.getGoogleSub().value() : null);
    entity.setVerificationToken(domain.getVerificationToken());
    entity.setVerificationTokenExpiresAt(domain.getVerificationTokenExpiresAt());
    return entity;
  }

  public UserAggregate toDomain(UserEntity entity) {
    GoogleSub googleSub =
        entity.getGoogleSub() != null ? GoogleSub.of(entity.getGoogleSub()) : null;

    return UserAggregate.reconstitute(
        UserId.of(entity.getId()),
        Username.of(entity.getUsername()),
        Email.of(entity.getEmail()),
        HashedPassword.fromHash(entity.getPassword()),
        entity.isActive(),
        entity.isEmailVerified(),
        googleSub,
        Timestamp.of(entity.getCreatedAt()),
        Timestamp.of(entity.getUpdatedAt()),
        entity.getRoles(),
        entity.getVerificationToken(),
        entity.getVerificationTokenExpiresAt());
  }
}