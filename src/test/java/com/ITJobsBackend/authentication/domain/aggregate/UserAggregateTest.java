package com.ITJobsBackend.authentication.domain.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.ITJobsBackend.authentication.domain.exceptions.UserAlreadyActivatedException;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.shared.domain.valueobjects.Email;

class UserAggregateTest {

  @Test
  void shouldCreateUserWithDefaultValues() {
    Username username = Username.of("johndoe");
    Email email = Email.of("john@example.com");
    HashedPassword password = HashedPassword.fromHash("$2a$10$hashed");

    UserAggregate user = UserAggregate.create(username, email, password);

    assertNotNull(user.getId());
    assertEquals(username, user.getUsername());
    assertEquals(email, user.getEmail());
    assertFalse(user.isActive());
    assertFalse(user.isEmailVerified());
    assertTrue(user.getRoles().contains("ROLE_USER"));
  }

  @Test
  void shouldActivateUser() {
    UserAggregate user =
        UserAggregate.create(
            Username.of("johndoe"),
            Email.of("john@example.com"),
            HashedPassword.fromHash("$2a$10$hashed"));

    user.activate();

    assertTrue(user.isActive());
  }

  @Test
  void shouldThrowExceptionWhenActivatingAlreadyActiveUser() {
    UserAggregate user =
        UserAggregate.create(
            Username.of("johndoe"),
            Email.of("john@example.com"),
            HashedPassword.fromHash("$2a$10$hashed"));
    user.activate();

    assertThrows(UserAlreadyActivatedException.class, user::activate);
  }

  @Test
  void shouldVerifyEmail() {
    UserAggregate user =
        UserAggregate.create(
            Username.of("johndoe"),
            Email.of("john@example.com"),
            HashedPassword.fromHash("$2a$10$hashed"));

    user.verifyEmail();

    assertTrue(user.isEmailVerified());
  }

  @Test
  void shouldAddRole() {
    UserAggregate user =
        UserAggregate.create(
            Username.of("johndoe"),
            Email.of("john@example.com"),
            HashedPassword.fromHash("$2a$10$hashed"));

    user.addRole("ROLE_ADMIN");

    assertTrue(user.getRoles().contains("ROLE_ADMIN"));
    assertEquals(2, user.getRoles().size());
  }

  @Test
  void shouldNotAddDuplicateRole() {
    UserAggregate user =
        UserAggregate.create(
            Username.of("johndoe"),
            Email.of("john@example.com"),
            HashedPassword.fromHash("$2a$10$hashed"));

    user.addRole("ROLE_USER");

    assertEquals(1, user.getRoles().size());
  }
}
