package com.risetogether.authentication.infrastructure.adapters.out.persistence.jpa;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.risetogether.authentication.domain.aggregate.UserAggregate;
import com.risetogether.authentication.domain.valueobjects.HashedPassword;
import com.risetogether.authentication.domain.valueobjects.Username;
import com.risetogether.authentication.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;
import com.risetogether.authentication.infrastructure.adapters.out.persistence.jpa.mappers.write.UserMapper;
import com.risetogether.shared.domain.valueobjects.Email;

/**
 * Unit test for {@link JpaUserRepositoryAdapter} — the domain-level CRUD adapter. Deletion is NOT
 * tested here because {@code delete} was moved out of the domain repository contract and is now
 * handled by {@code DeleteUserPortAdapter} directly via {@link
 * SpringDataJpaUserRepository#deleteById}.
 */
@ExtendWith(MockitoExtension.class)
class JpaUserRepositoryAdapterTest {

  private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final String USERNAME = "johndoe";
  private static final String EMAIL = "john@example.com";
  private static final String PASSWORD_HASH = "$2a$10$hashedpassword";

  @Mock private SpringDataJpaUserRepository jpaRepository;
  @Mock private UserMapper mapper;
  @InjectMocks private JpaUserRepositoryAdapter adapter;

  private UserAggregate userAggregate;
  private UserEntity userEntity;

  @BeforeEach
  void setUp() {
    userAggregate =
        UserAggregate.create(
            Username.of(USERNAME), Email.of(EMAIL), HashedPassword.fromHash(PASSWORD_HASH));
    userEntity = new UserEntity();
    userEntity.setId(USER_ID);
    userEntity.setUsername(USERNAME);
    userEntity.setEmail(EMAIL);
    userEntity.setPassword(PASSWORD_HASH);
    userEntity.setActive(true);
    userEntity.setEmailVerified(false);
    userEntity.setCreatedAt(Instant.now().minusSeconds(86400 * 2));
    userEntity.setUpdatedAt(Instant.now());
  }

  @Test
  void shouldSaveUser() {
    when(mapper.toEntity(userAggregate)).thenReturn(userEntity);
    when(jpaRepository.save(userEntity)).thenReturn(userEntity);
    when(mapper.toDomain(userEntity)).thenReturn(userAggregate);

    adapter.save(userAggregate);

    verify(jpaRepository).save(userEntity);
  }
}
