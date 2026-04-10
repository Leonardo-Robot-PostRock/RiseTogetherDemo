package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.mappers;

import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;
import java.util.ArrayList;
import org.springframework.stereotype.Component;

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
    return entity;
  }

  public UserAggregate toDomain(UserEntity entity) {
    return UserAggregate.reconstitute(
        UserId.of(entity.getId()),
        Username.of(entity.getUsername()),
        Email.of(entity.getEmail()),
        HashedPassword.fromHash(entity.getPassword()),
        entity.isActive(),
        entity.isEmailVerified(),
        Timestamp.of(entity.getCreatedAt()),
        Timestamp.of(entity.getUpdatedAt()),
        entity.getRoles());
  }
}
