package com.ITJobsBackend.authentication.domain.aggregate;

import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.authentication.domain.exceptions.UserAlreadyActivatedException;
import com.ITJobsBackend.authentication.domain.exceptions.UserAlreadyDeactivatedException;
import com.ITJobsBackend.authentication.domain.event.UserActivatedEvent;
import com.ITJobsBackend.shared.domain.event.DomainEvent;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserAggregate {
    private final UserId id;
    private Username username;
    private Email email;
    private HashedPassword password;
    private boolean active;
    private boolean emailVerified;
    private final Timestamp createdAt;
    private Timestamp updatedAt;
    private final List<String> roles;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private UserAggregate(
        UserId id,
        Username username,
        Email email,
        HashedPassword password,
        Timestamp createdAt
    ) {
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
        Username username,
        Email email,
        HashedPassword hashedPassword
    ) {
        return new UserAggregate(
            UserId.generate(),
            username,
            email,
            hashedPassword,
            Timestamp.now()
        );
    }

    public static UserAggregate reconstitute(
        UserId id,
        Username username,
        Email email,
        HashedPassword password,
        boolean active,
        boolean emailVerified,
        Timestamp createdAt,
        Timestamp updatedAt,
        List<String> roles
    ) {
        UserAggregate user = new UserAggregate(id, username, email, password, createdAt);
        user.active = active;
        user.emailVerified = emailVerified;
        user.updatedAt = updatedAt;
        user.roles.clear();
        user.roles.addAll(roles);
        return user;
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
    }

    public void verifyEmail() {
        this.emailVerified = true;
        this.updatedAt = Timestamp.now();
    }

    public void changePassword(HashedPassword newPassword) {
        this.password = newPassword;
        this.updatedAt = Timestamp.now();
    }

    public void updateEmail(Email newEmail) {
        this.email = newEmail;
        this.emailVerified = false;
        this.updatedAt = Timestamp.now();
    }

    public void addRole(String role) {
        if (!this.roles.contains(role)) {
            this.roles.add(role);
            this.updatedAt = Timestamp.now();
        }
    }

    public UserId getId() { return id; }
    public Username getUsername() { return username; }
    public Email getEmail() { return email; }
    public HashedPassword getPassword() { return password; }
    public boolean isActive() { return active; }
    public boolean isEmailVerified() { return emailVerified; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public List<String> getRoles() { return Collections.unmodifiableList(roles); }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    private void recordEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }
}
