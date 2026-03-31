package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.entities;

import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "users",
    indexes = {
      @Index(name = "idx_email", columnList = "email", unique = true),
      @Index(name = "idx_username", columnList = "username", unique = true)
    })
@Getter
@NoArgsConstructor
public class UserEntity {
  @Id
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(columnDefinition = "CHAR(36)")
  private UUID id;

  @Setter
  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Setter
  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Setter
  @Column(nullable = false, length = 255)
  private String password;

  @Setter
  @Column(nullable = false)
  private boolean active;

  @Setter
  @Column(name = "email_verified", nullable = false)
  private boolean emailVerified;

  @Setter
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Setter
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Setter
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "role")
  private List<String> roles = new ArrayList<>();

  public static UserEntity fromDomain(UserAggregate user) {
    UserEntity entity = new UserEntity();
    entity.id = user.getId().value();
    entity.username = user.getUsername().value();
    entity.email = user.getEmail().value();
    entity.password = user.getPassword().value();
    entity.active = user.isActive();
    entity.emailVerified = user.isEmailVerified();
    entity.createdAt = user.getCreatedAt().value();
    entity.updatedAt = user.getUpdatedAt().value();
    entity.roles = new ArrayList<>(user.getRoles());
    return entity;
  }
}
