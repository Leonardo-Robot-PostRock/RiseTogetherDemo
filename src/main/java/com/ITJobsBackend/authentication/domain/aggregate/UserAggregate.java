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
import com.ITJobsBackend.authentication.domain.valueobjects.UserStatus;
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
  private UserStatus status;
  private GoogleSub googleSub;
  private Timestamp updatedAt;
  private VerificationToken verificationToken;

  private UserAggregate(
      UserId id, Username username, Email email, HashedPassword password, Timestamp createdAt) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.status = UserStatus.PENDING_VERIFICATION;
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
    user.status = UserStatus.ACTIVE;

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

    user.status = UserStatus.from(active, emailVerified);
    user.googleSub = googleSub;
    user.updatedAt = updatedAt;
    user.roles.clear();
    user.roles.addAll(roles);
    user.verificationToken = verificationToken;

    return user;
  }

  public void assignVerificationToken(VerificationToken verificationToken) {
    if (this.status.isEmailVerified()) {
      throw new EmailAlreadyVerifiedException(
          "Cannot assign a verification token to an already verified email");
    }

    this.verificationToken = verificationToken;
    this.updatedAt = Timestamp.now();
  }

  public void activate() {
    if (this.status == UserStatus.ACTIVE) {
      throw new UserAlreadyActivatedException("User is already active");
    }

    this.status = UserStatus.ACTIVE;
    this.updatedAt = Timestamp.now();

    recordEvent(new UserActivatedEvent(this.id, this.email));
  }

  public void deactivate() {
    if (this.status != UserStatus.ACTIVE) {
      throw new UserAlreadyDeactivatedException("User is already deactivated");
    }

    this.status = UserStatus.SUSPENDED;
    this.updatedAt = Timestamp.now();

    recordEvent(new UserDeactivatedEvent(this.id, this.email));
  }

  public void verifyEmail() {
    if (this.status.isEmailVerified()) {
      throw new EmailAlreadyVerifiedException("Email is already verified");
    }

    this.status = UserStatus.ACTIVE;
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
    this.status = UserStatus.PENDING_VERIFICATION;
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

  public UserStatus getStatus() {
    return status;
  }

  public boolean isActive() {
    return status.isActive();
  }

  public boolean isEmailVerified() {
    return status.isEmailVerified();
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
        + ", email='"
        + email.mask()
        + '\''
        + ", status="
        + status
        + ", roles="
        + roles
        + '}';
  }
}
