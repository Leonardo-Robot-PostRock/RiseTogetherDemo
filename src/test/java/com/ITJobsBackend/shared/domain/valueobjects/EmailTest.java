package com.ITJobsBackend.shared.domain.valueobjects;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateValidEmail() {
        Email email = Email.of("test@example.com");
        assertEquals("test@example.com", email.value());
    }

    @Test
    void shouldNormalizeToLowerCase() {
        Email email = Email.of("Test@EXAMPLE.com");
        assertEquals("test@example.com", email.value());
    }

    @Test
    void shouldThrowOnEmptyEmail() {
        assertThrows(ValidationException.class, () -> Email.of(""));
    }

    @Test
    void shouldThrowOnNullEmail() {
        assertThrows(ValidationException.class, () -> Email.of(null));
    }

    @Test
    void shouldThrowOnInvalidFormat() {
        assertThrows(ValidationException.class, () -> Email.of("invalid-email"));
    }

    @Test
    void shouldBeEqualForSameValue() {
        Email email1 = Email.of("test@example.com");
        Email email2 = Email.of("test@example.com");
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }
}
