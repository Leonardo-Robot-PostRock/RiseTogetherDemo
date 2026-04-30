package com.risetogether.shared.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Flyway configuration removed in Spring Boot 4.
 * FlywayMigrationStrategy was deprecated and removed.
 * Use spring.flyway.clean-disabled and other properties instead.
 */
@Configuration
public class FlywayConfig {
    // Flyway auto-configuration handles migration automatically
    // Custom strategy no longer supported in Spring Boot 4
}