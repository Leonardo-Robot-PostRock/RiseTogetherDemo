package com.ITJobsBackend.authentication.domain.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.ITJobsBackend.authentication.domain.exceptions.UserAlreadyActivatedException;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.domain.valueobjects.Email;

class UserAggregateTest {

  private UserAggregate createInactiveUser() {
    return UserAggregate.create(
        Username.of("johndoe"),
        Email.of("john@example.com"),
        HashedPassword.fromHash("$2a$10$hashed"));
  }

  @Test
  void shouldCreateUserWithDefaultValues() {
    // Given
    Username username = Username.of("johndoe");
    Email email = Email.of("john@example.com");
    HashedPassword password = HashedPassword.fromHash("$2a$10$hashed");

    // When
    UserAggregate user = UserAggregate.create(username, email, password);

    // Then
    assertNotNull(user.getId());
    assertEquals(username, user.getUsername());
    assertEquals(email, user.getEmail());
    assertFalse(user.isActive());
    assertFalse(user.isEmailVerified());
    assertTrue(user.getRoles().contains("ROLE_USER"));
  }

  @Test
  void shouldActivateUser() {
    // Given
    UserAggregate user = createInactiveUser();

    // When
    user.activate();

    // Then
    assertTrue(user.isActive());
  }

  @Test
  void shouldThrowExceptionWhenActivatingAlreadyActiveUser() {
    // Given
    UserAggregate user = createInactiveUser();
    user.activate();

    // When & Then
    assertThrows(UserAlreadyActivatedException.class, user::activate);
  }

  @Test
  void shouldVerifyEmail() {
    // Given
    UserAggregate user = createInactiveUser();

    // When
    user.verifyEmail();

    // Then
    assertTrue(user.isEmailVerified());
  }

  @Test
  void shouldAddRole() {
    // Given
    UserAggregate user = createInactiveUser();

    // When
    user.addRole("ROLE_ADMIN");

    // Then
    assertTrue(user.getRoles().contains("ROLE_ADMIN"));
    assertEquals(2, user.getRoles().size());
  }

  @Test
  void shouldNotAddDuplicateRole() {
    // Given
    UserAggregate user = createInactiveUser();

    // When
    user.addRole("ROLE_USER");

    // Then
    assertEquals(1, user.getRoles().size());
  }
}
