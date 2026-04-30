# Rise Together — Domain Testing

> Patrón para tests de dominio (Value Objects, Aggregates) sin Spring.

## Reglas

- No usar Spring ni Mockito en tests de dominio
- Usar JUnit 5 puro con assertiones de `org.junit.jupiter.api.Assertions`
- Estructura: Given / When / Then

## Value Objects

```java
class EmailTest {

    @Test
    void shouldNormalizeToLowerCase() {
        // When
        Email email = Email.of("Test@EXAMPLE.com");

        // Then
        assertEquals("test@example.com", email.value());
    }

    @Test
    void shouldThrowOnInvalidFormat() {
        // When & Then
        assertThrows(ValidationException.class, () -> Email.of("invalid-email"));
    }
}
```

## Aggregates

```java
class UserAggregateTest {

    private UserAggregate createInactiveUser() {
        return UserAggregate.create(
            Username.of("johndoe"),
            Email.of("john@example.com"),
            HashedPassword.fromHash("$2a$10$hashed"));
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
    void shouldRecordDomainEventWhenActivated() {
        // Given
        UserAggregate user = createInactiveUser();

        // When
        user.activate();

        // Then
        assertEquals(1, user.pullDomainEvents().size());
    }
}
```

## Convenciones

| Elemento | Convención |
|----------|------------|
| Clase test | `<ClaseProbada>Test` |
| Método | `should<Comportamiento>` |
| Helper construcción | `createXxx()` |
| Constantes | `UPPER_SNAKE_CASE` |

## Errores a Evitar

- ❌ No usar `@Mock` en domain
- ❌ No usar `@InjectMocks`
- ❌ No inyectar puertos — construir objetos reales

## Referencia

- `itjobs-docs/shared/testing.md` — sección 4
- `src/test/java/com/Rise Together/authentication/domain/aggregate/UserAggregateTest.java`