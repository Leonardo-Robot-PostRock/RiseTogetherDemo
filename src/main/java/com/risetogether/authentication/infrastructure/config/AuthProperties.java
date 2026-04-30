package com.risetogether.authentication.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "auth")
@Getter
@Setter
public class AuthProperties {

  /**
   * Number of hours the email verification token remains valid after user registration.
   *
   * <p>When a new user registers, {@code UserRegisteredEventListener} generates a
   * one-time token and stores it on the user. The token expires after this many hours.
   * Once expired, the user must request a new verification email.
   *
   * <p>Default: 24 hours.
   * Override with property: {@code auth.verification-token-expiration-hours}
   */
  private int verificationTokenExpirationHours = 24;
}
