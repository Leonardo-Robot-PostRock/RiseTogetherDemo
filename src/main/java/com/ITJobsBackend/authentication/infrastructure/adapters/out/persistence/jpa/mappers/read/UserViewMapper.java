package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.mappers.read;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ITJobsBackend.authentication.application.query.UserView;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

/** Maps {@link UserEntity} → {@link UserView} (CQRS read side). */
@Component
public class UserViewMapper {

  public UserView toView(UserEntity entity) {
    return new UserView(
        UserId.of(entity.getId()),
        entity.getUsername(),
        entity.getEmail(),
        HashedPassword.fromHash(entity.getPassword()),
        entity.isActive(),
        entity.isEmailVerified(),
        List.copyOf(entity.getRoles()));
  }
}
