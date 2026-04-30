package com.risetogether.authentication.infrastructure.adapters.out.persistence.jpa.mappers.write;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.risetogether.authentication.domain.aggregate.UserAggregate;
import com.risetogether.authentication.domain.valueobjects.GoogleSub;
import com.risetogether.authentication.domain.valueobjects.HashedPassword;
import com.risetogether.authentication.domain.valueobjects.Username;
import com.risetogether.authentication.domain.valueobjects.VerificationToken;
import com.risetogether.authentication.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;
import com.risetogether.shared.domain.valueobjects.Email;
import com.risetogether.shared.domain.valueobjects.Timestamp;
import com.risetogether.shared.domain.valueobjects.UserId;

/** Maps {@link UserAggregate} ↔ {@link UserEntity} (CQRS write side). */
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
    entity.setVerificationToken(
        domain.getVerificationToken() != null ? domain.getVerificationToken().token() : null);
    entity.setVerificationTokenExpiresAt(
        domain.getVerificationToken() != null ? domain.getVerificationToken().expiresAt() : null);
    return entity;
  }

  public UserAggregate toDomain(UserEntity entity) {
    GoogleSub googleSub =
        entity.getGoogleSub() != null ? GoogleSub.of(entity.getGoogleSub()) : null;

    VerificationToken verificationToken =
        entity.getVerificationToken() != null
            ? VerificationToken.of(
                entity.getVerificationToken(), entity.getVerificationTokenExpiresAt())
            : null;

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
        verificationToken);
  }
}
