package com.ITJobsBackend.authentication.domain.aggregate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ITJobsBackend.authentication.domain.event.EmailVerifiedEvent;
import com.ITJobsBackend.authentication.domain.event.UserActivatedEvent;
import com.ITJobsBackend.authentication.domain.event.UserDeactivatedEvent;
import com.ITJobsBackend.authentication.domain.exceptions.EmailAlreadyVerifiedException;
import com.ITJobsBackend.authentication.domain.exceptions.UserAlreadyActivatedException;
import com.ITJobsBackend.authentication.domain.exceptions.UserAlreadyDeactivatedException;
import com.ITJobsBackend.authentication.domain.exceptions.VerificationTokenExpiredException;
import com.ITJobsBackend.authentication.domain.valueobjects.GoogleSub;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
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
  private String verificationToken;
  private Instant verificationTokenExpiresAt;

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
      String verificationToken,
      Instant verificationTokenExpiresAt) {

    UserAggregate user = new UserAggregate(id, username, email, password, createdAt);

    user.active = active;
    user.emailVerified = emailVerified;
    user.googleSub = googleSub;
    user.updatedAt = updatedAt;
    user.roles.clear();
    user.roles.addAll(roles);
    user.verificationToken = verificationToken;
    user.verificationTokenExpiresAt = verificationTokenExpiresAt;
    return user;
  }

  public void setVerificationToken(String token, Instant expiresAt) {
    this.verificationToken = token;
    this.verificationTokenExpiresAt = expiresAt;
  }

  public void activate() {
    if (this.active) {
      throw new UserAlreadyActivatedException("User is already active");
    }
    this.active = true;
    this.updatedAt = Timestamp.now();
    recordEvent(new UserActivatedEvent(this.id.value().toString(), this.email.value()));
  }

  public void deactivate() {
    if (!this.active) {
      throw new UserAlreadyDeactivatedException("User is already deactivated");
    }

    this.active = false;
    this.updatedAt = Timestamp.now();
    recordEvent(new UserDeactivatedEvent(this.id.value().toString(), this.email.value()));
  }

  public void verifyEmail(String providedToken) {
    if (this.emailVerified) {
      throw new EmailAlreadyVerifiedException("Email is already verified");
    }

    if (verificationToken == null || !verificationToken.equals(providedToken)) {
      throw new IllegalArgumentException("Invalid verification token");
    }

    if (verificationTokenExpiresAt != null
        && Instant.now().isAfter(verificationTokenExpiresAt)) {
      throw new VerificationTokenExpiredException("Verification token has expired");
    }

    this.emailVerified = true;
    this.updatedAt = Timestamp.now();
    recordEvent(new EmailVerifiedEvent(this.id.value().toString(), this.email.value()));
  }

  public void changePassword(HashedPassword newPassword) {
    this.password = newPassword;
    this.updatedAt = Timestamp.now();
  }

  public void linkGoogleAccount(GoogleSub googleSub) {
    this.googleSub = googleSub;
    this.updatedAt = Timestamp.now();
  }

  public void updateEmail(Email newEmail) {
    this.email = newEmail;
    this.emailVerified = false;
    this.updatedAt = Timestamp.now();
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

  public String getVerificationToken() {
    return verificationToken;
  }

  public Instant getVerificationTokenExpiresAt() {
    return verificationTokenExpiresAt;
  }
}
