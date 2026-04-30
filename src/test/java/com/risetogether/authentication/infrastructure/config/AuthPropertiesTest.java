package com.risetogether.authentication.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

class AuthPropertiesTest {

    @Nested
    @SpringBootTest(classes = AuthProperties.class)
    @EnableConfigurationProperties(AuthProperties.class)
    @TestPropertySource(
        properties = "auth.verification-token-expiration-hours=48",
        inheritProperties = false
    )
    class WithCustomProperty {

        @Autowired
        private AuthProperties authProperties;

        @Test
        void shouldBindEmailVerificationTokenExpirationHoursFromProperty() {
            assertEquals(48, authProperties.getVerificationTokenExpirationHours());
        }
    }

    @Nested
    @SpringBootTest(classes = AuthProperties.class)
    @EnableConfigurationProperties(AuthProperties.class)
    @TestPropertySource(
        properties = "auth.verification-token-expiration-hours=1",
        inheritProperties = false
    )
    class WithPropertyFromTestConfig {

        @Autowired
        private AuthProperties authProperties;

        @Test
        void shouldBindEmailVerificationTokenExpirationHoursFromTestProperty() {
            assertEquals(1, authProperties.getVerificationTokenExpirationHours());
        }
    }
}