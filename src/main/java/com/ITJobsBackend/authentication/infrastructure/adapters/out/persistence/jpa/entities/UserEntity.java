package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;

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
  @Setter
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

  @Setter
  @Column(name = "google_sub", unique = true, length = 255)
  private String googleSub;
}
