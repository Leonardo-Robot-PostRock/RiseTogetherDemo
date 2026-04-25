package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.authentication.domain.repository.UserReaderRepository;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class LoadUserPortAdapterTest {

  private static final String EMAIL = "john@example.com";

  @Mock private UserReaderRepository userRepository;
  @InjectMocks private LoadUserPortAdapter adapter;

  private UserAggregate buildUser() {
    return UserAggregate.create(
        Username.of("johndoe"), Email.of(EMAIL), HashedPassword.fromHash("$2a$10$hashed"));
  }

  @Test
  void shouldFindByEmail() {
    UserAggregate user = buildUser();
    when(userRepository.findByEmail(Email.of(EMAIL))).thenReturn(Optional.of(user));

    Optional<UserAggregate> result = adapter.findByEmail(Email.of(EMAIL));

    assertTrue(result.isPresent());
    assertEquals(user, result.get());
    verify(userRepository).findByEmail(Email.of(EMAIL));
  }

  @Test
  void shouldReturnEmptyWhenEmailNotFound() {
    when(userRepository.findByEmail(Email.of(EMAIL))).thenReturn(Optional.empty());

    Optional<UserAggregate> result = adapter.findByEmail(Email.of(EMAIL));

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldFindById() {
    UserAggregate user = buildUser();
    UserId userId = user.getId();
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    Optional<UserAggregate> result = adapter.findById(userId);

    assertTrue(result.isPresent());
    assertEquals(user, result.get());
    verify(userRepository).findById(userId);
  }

  @Test
  void shouldReturnEmptyWhenIdNotFound() {
    UserId userId = UserId.generate();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    Optional<UserAggregate> result = adapter.findById(userId);

    assertTrue(result.isEmpty());
  }
}
