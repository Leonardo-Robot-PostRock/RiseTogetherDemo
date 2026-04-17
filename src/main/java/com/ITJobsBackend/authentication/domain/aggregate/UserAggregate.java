package com.ITJobsBackend.authentication.domain.aggregate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ITJobsBackend.authentication.domain.event.EmailChangedEvent;
import com.ITJobsBackend.authentication.domain.event.EmailVerifiedEvent;
import com.ITJobsBackend.authentication.domain.event.GoogleAccountLinkedEvent;
import com.ITJobsBackend.authentication.domain.event.PasswordChangedEvent;
import com.ITJobsBackend.authentication.domain.event.UserActivatedEvent;
import com.ITJobsBackend.authentication.domain.event.UserDeactivatedEvent;
import com.ITJobsBackend.authentication.domain.exceptions.EmailAlreadyVerifiedException;
import com.ITJobsBackend.authentication.domain.exceptions.UserAlreadyActivatedException;
import com.ITJobsBackend.authentication.domain.exceptions.UserAlreadyDeactivatedException;
import com.ITJobsBackend.authentication.domain.valueobjects.GoogleSub;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.authentication.domain.valueobjects.VerificationToken;
import com.ITJobsBackend.shared.domain.AggregateRoot;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

public class UserAggregate extends AggregateRoot {
  private final UserId id;
  private final Timestamp createdAt;
  private final List<String> roles;
  private Username username;
  private Email email;
  private HashedPassword password;
  private boolean active;
  private boolean emailVerified;
  private GoogleSub googleSub;
  private Timestamp updatedAt;
  private VerificationToken verificationToken;

  private UserAggregate(
      UserId id, Username username, Email email, HashedPassword password, Timestamp createdAt) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.active = false;
    this.emailVerified = false;
    this.createdAt = createdAt;
    this.updatedAt = createdAt;
    this.roles = new ArrayList<>();
    this.roles.add("ROLE_USER");
  }

  public static UserAggregate create(
      Username username, Email email, HashedPassword hashedPassword) {
    return new UserAggregate(UserId.generate(), username, email, hashedPassword, Timestamp.now());
  }

  public static UserAggregate createGoogleUser(
      Username username, Email email, HashedPassword hashedPassword, GoogleSub googleSub) {
    UserAggregate user =
        new UserAggregate(UserId.generate(), username, email, hashedPassword, Timestamp.now());

    user.googleSub = googleSub;
    user.active = true;
    user.emailVerified = true;
    return user;
  }

  public static UserAggregate reconstitute(
      UserId id,
      Username username,
      Email email,
      HashedPassword password,
      boolean active,
      boolean emailVerified,
      GoogleSub googleSub,
      Timestamp createdAt,
      Timestamp updatedAt,
      List<String> roles,
      VerificationToken verificationToken) {

    UserAggregate user = new UserAggregate(id, username, email, password, createdAt);

    user.active = active;
    user.emailVerified = emailVerified;
    user.googleSub = googleSub;
    user.updatedAt = updatedAt;
    user.roles.clear();
    user.roles.addAll(roles);
    user.verificationToken = verificationToken;
    return user;
  }

  public void assignVerificationToken(VerificationToken verificationToken) {
    if (this.emailVerified) {
      throw new EmailAlreadyVerifiedException("Cannot assign a verification token to an already verified email");
    }
    this.verificationToken = verificationToken;
    this.updatedAt = Timestamp.now();
  }

  public void activate() {
    if (this.active) {
      throw new UserAlreadyActivatedException("User is already active");
    }
    this.active = true;
    this.updatedAt = Timestamp.now();

    recordEvent(new UserActivatedEvent(this.id, this.email));
  }

  public void deactivate() {
    if (!this.active) {
      throw new UserAlreadyDeactivatedException("User is already deactivated");
    }

    this.active = false;
    this.updatedAt = Timestamp.now();

    recordEvent(new UserDeactivatedEvent(this.id, this.email));
  }

  public void verifyEmail() {
    if (this.emailVerified) {
      throw new EmailAlreadyVerifiedException("Email is already verified");
    }

    this.emailVerified = true;
    this.updatedAt = Timestamp.now();

    recordEvent(new EmailVerifiedEvent(this.id, this.email));
  }

  public void changePassword(HashedPassword newPassword) {
    this.password = newPassword;
    this.updatedAt = Timestamp.now();

    recordEvent(new PasswordChangedEvent(this.id));
  }

  public void linkGoogleAccount(GoogleSub googleSub) {
    this.googleSub = googleSub;
    this.updatedAt = Timestamp.now();

    recordEvent(new GoogleAccountLinkedEvent(this.id, googleSub));
  }

  public void updateEmail(Email newEmail) {
    this.email = newEmail;
    this.emailVerified = false;
    this.updatedAt = Timestamp.now();

    recordEvent(new EmailChangedEvent(this.id, this.email));
  }
  
  public void addRole(String role) {
    String normalized = role.trim().toUpperCase();

    if (!this.roles.contains(normalized)) {
      this.roles.add(normalized);
      this.updatedAt = Timestamp.now();
    }
  }

  public UserId getId() {
    return id;
  }

  public Username getUsername() {
    return username;
  }

  public Email getEmail() {
    return email;
  }

  public HashedPassword getPassword() {
    return password;
  }

  public boolean isActive() {
    return active;
  }

  public boolean isEmailVerified() {
    return emailVerified;
  }

  public GoogleSub getGoogleSub() {
    return googleSub;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public List<String> getRoles() {
    return Collections.unmodifiableList(roles);
  }

  public VerificationToken getVerificationToken() {
    return verificationToken;
  }

  @Override
  public String toString() {
    return "UserAggregate{"
        + "id="
        + id
        + ", username="
        + username
        + ", email=[PROTECTED]"
        + email.mask()
        + ", active="
        + active
        + ", emailVerified="
        + emailVerified
        + ", roles="
        + roles
        + '}';
  }
}
