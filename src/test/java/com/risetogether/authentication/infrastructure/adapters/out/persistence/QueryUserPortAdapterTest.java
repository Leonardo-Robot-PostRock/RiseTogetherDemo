package com.risetogether.authentication.infrastructure.adapters.out.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.risetogether.authentication.application.query.UserView;
import com.risetogether.authentication.domain.valueobjects.HashedPassword;
import com.risetogether.authentication.infrastructure.adapters.out.persistence.jpa.SpringDataJpaUserRepository;
import com.risetogether.authentication.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;
import com.risetogether.authentication.infrastructure.adapters.out.persistence.jpa.mappers.read.UserViewMapper;
import com.risetogether.shared.domain.valueobjects.Email;
import com.risetogether.shared.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class QueryUserPortAdapterTest {

  private static final UUID USER_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final String EMAIL = "john@example.com";
  private static final String PASSWORD = "$2a$10$hashedpassword";

  @Mock private SpringDataJpaUserRepository jpaRepository;
  @Mock private UserViewMapper userViewMapper;
  @InjectMocks private QueryUserPortAdapter adapter;

  private UserEntity userEntity;

  @BeforeEach
  void setUp() {
    userEntity = new UserEntity();
    userEntity.setId(USER_UUID);
    userEntity.setEmail(EMAIL);
    userEntity.setUsername("johndoe");
    userEntity.setPassword(PASSWORD);
    userEntity.setActive(true);
    userEntity.setEmailVerified(true);
    userEntity.setRoles(List.of("ROLE_USER"));
  }

  @Test
  void shouldFindByEmailAndMapToView() {
    UserView expectedView = new UserView(
        UserId.of(USER_UUID), "johndoe", EMAIL,
        HashedPassword.fromHash(PASSWORD), true, true, List.of("ROLE_USER"));
    when(jpaRepository.findByEmail(EMAIL)).thenReturn(Optional.of(userEntity));
    when(userViewMapper.toView(userEntity)).thenReturn(expectedView);

    Optional<UserView> result = adapter.findByEmail(Email.of(EMAIL));

    assertTrue(result.isPresent());
    UserView view = result.get();
    assertEquals(UserId.of(USER_UUID), view.id());
    assertEquals("johndoe", view.username());
    assertEquals(EMAIL, view.email());
    assertEquals(PASSWORD, view.hashedPassword().value());
    assertTrue(view.active());
    assertTrue(view.emailVerified());
    assertEquals(List.of("ROLE_USER"), view.roles());
    verify(jpaRepository).findByEmail(EMAIL);
    verify(userViewMapper).toView(userEntity);
  }

  @Test
  void shouldReturnEmptyWhenEmailNotFound() {
    when(jpaRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

    assertTrue(adapter.findByEmail(Email.of(EMAIL)).isEmpty());
  }

  @Test
  void shouldFindByIdAndMapToView() {
    UserView expectedView = new UserView(
        UserId.of(USER_UUID), "johndoe", EMAIL,
        HashedPassword.fromHash(PASSWORD), true, true, List.of("ROLE_USER"));
    when(jpaRepository.findById(USER_UUID)).thenReturn(Optional.of(userEntity));
    when(userViewMapper.toView(userEntity)).thenReturn(expectedView);

    Optional<UserView> result = adapter.findById(UserId.of(USER_UUID));

    assertTrue(result.isPresent());
    assertEquals(EMAIL, result.get().email());
    verify(userViewMapper).toView(userEntity);
  }

  @Test
  void shouldReturnTrueWhenEmailExists() {
    when(jpaRepository.existsByEmail(EMAIL)).thenReturn(true);

    assertTrue(adapter.existsByEmail(Email.of(EMAIL)));
    verify(jpaRepository).existsByEmail(EMAIL);
  }

  @Test
  void shouldReturnFalseWhenEmailDoesNotExist() {
    when(jpaRepository.existsByEmail(EMAIL)).thenReturn(false);

    assertFalse(adapter.existsByEmail(Email.of(EMAIL)));
  }

  @Test
  void shouldReturnUnverifiedUserIdsBefore() {
    Instant cutoff = Instant.now().minusSeconds(86400);
    when(jpaRepository.findUnverifiedUserIdsBefore(cutoff)).thenReturn(List.of(USER_UUID));

    List<UserId> result = adapter.findUnverifiedUserIdsBefore(cutoff);

    assertEquals(1, result.size());
    assertEquals(UserId.of(USER_UUID), result.get(0));
    verify(jpaRepository).findUnverifiedUserIdsBefore(cutoff);
  }

  @Test
  void shouldReturnEmptyListWhenNoUnverifiedUsers() {
    Instant cutoff = Instant.now();
    when(jpaRepository.findUnverifiedUserIdsBefore(cutoff)).thenReturn(List.of());

    assertTrue(adapter.findUnverifiedUserIdsBefore(cutoff).isEmpty());
  }
}
