# Testing Guide — ITJobsBackend

## Índice

1. [Estrategia general](#1-estrategia-general)
2. [Tipos de tests](#2-tipos-de-tests)
3. [BDD Style: Given / When / Then](#3-bdd-style-given--when--then)
4. [Tests de dominio (sin Spring)](#4-tests-de-dominio-sin-spring)
5. [Tests de casos de uso (con Mockito BDD)](#5-tests-de-casos-de-uso-con-mockito-bdd)
6. [Patrones de estructura](#6-patrones-de-estructura)
7. [Convenciones de nombres](#7-convenciones-de-nombres)
8. [Anti-patrones a evitar](#8-anti-patrones-a-evitar)
9. [Referencia rápida de BDDMockito](#9-referencia-rápida-de-bddmockito)

---

## 1. Estrategia general

Este proyecto usa tests unitarios puros sin contexto de Spring. Todas las clases del dominio
y los casos de uso se pueden testear de forma aislada gracias a la Arquitectura Hexagonal.

| Capa | Tipo de test | Framework |
|---|---|---|
| Value objects (`Email`, `Salary`, …) | Unitario puro | JUnit 5 |
| Aggregates (`UserAggregate`, `JobAggregate`) | Unitario puro | JUnit 5 |
| Use cases (`LoginUseCase`, …) | Unitario con mocks | JUnit 5 + Mockito BDD |
| Infraestructura / integración | Integración con H2 | Spring Boot Test |

---

## 2. Tipos de tests

### Tests de dominio
- No usan Spring ni Mockito
- Testean invariantes del agregado y validaciones del value object
- Rápidos, sin dependencias externas

### Tests de casos de uso
- Usan `@ExtendWith(MockitoExtension.class)`
- Mockean los puertos de salida (`LoadUserPort`, `SaveUserPort`, etc.)
- Verifican que el use case orquesta los puertos correctamente

### Tests de integración
- Ubicados en `src/test/resources/application.properties` con H2 en memoria
- `spring.jpa.hibernate.ddl-auto=create-drop`

---

## 3. BDD Style: Given / When / Then

Todo test debe estructurarse con tres secciones claramente separadas:

```
// Given  → estado inicial y stubs de mocks
// When   → acción que se ejecuta (llamada al método bajo test)
// Then   → assertions y verificaciones
```

Cuando el `When` y el `Then` ocurren en la misma línea (p.ej., `assertThrows`), se usa:

```
// When & Then
assertThrows(SomeException.class, () -> subject.action());
```

### ¿Por qué BDD?
- Separa visualmente el **setup**, la **acción** y la **verificación**
- Cualquier persona entiende qué comportamiento se está describiendo
- El test actúa como **especificación viva** del dominio
- Al fallar, se sabe exactamente en qué fase falló

---

## 4. Tests de dominio (sin Spring)

### Value objects

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

    @Test
    void shouldBeEqualForSameValue() {
        // Given
        Email email1 = Email.of("test@example.com");
        Email email2 = Email.of("test@example.com");

        // Then
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }
}
```

**Reglas:**
- Testear el factory method (`of()`, `fromHash()`)
- Testear normalización (toLowerCase, trim, etc.)
- Testear `equals` / `hashCode`
- Testear cada caso de validación inválida por separado

### Aggregates

```java
class UserAggregateTest {

    // Helper: extrae la construcción repetida
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
    void shouldThrowExceptionWhenActivatingAlreadyActiveUser() {
        // Given
        UserAggregate user = createInactiveUser();
        user.activate();

        // When & Then
        assertThrows(UserAlreadyActivatedException.class, user::activate);
    }
}
```

**Reglas:**
- Extraer un helper privado `createXxx()` para construir el agregado
- Testear cada método de comportamiento (`activate`, `close`, `verifyEmail`, etc.)
- Testear que se lanzan las excepciones de dominio correctas
- Testear que los domain events se registran (`pullDomainEvents()`)

---

## 5. Tests de casos de uso (con Mockito BDD)

### Imports estándar

```java
import static org.mockito.BDDMockito.given;        // stub de métodos con retorno: given(mock.m()).willReturn(v)
import static org.mockito.BDDMockito.willAnswer;   // stub con lógica dinámica (void o retorno): willAnswer(fn).given(mock).m()
import static org.mockito.BDDMockito.willDoNothing; // stub de métodos void: willDoNothing().given(mock).m()
import static org.mockito.BDDMockito.willThrow;    // stub que lanza excepción: willThrow(ex).given(mock).m()
import static org.mockito.BDDMockito.then;         // verificación BDD: then(mock).should().m()
import static org.mockito.Mockito.any;             // matcher genérico
import static org.mockito.Mockito.never;           // verificar que NO se llamó
```

> `BDDMockito` es parte de Mockito (`mockito-core`), no requiere dependencia adicional.
> `willReturn(v)` en `given(mock.m()).willReturn(v)` es un método de **instancia** encadenado
> al resultado de `given()` — no se importa de forma estática.

### Estructura de un test de use case

```java
@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    // ── Constantes ────────────────────────────────────────────────────────────
    private static final String EMAIL = "john@example.com";
    private static final String HASHED_PASSWORD = "$2a$10$hashed";

    // ── Mocks ─────────────────────────────────────────────────────────────────
    @Mock private LoadUserPort loadUserPort;
    @Mock private PasswordEncoderPort passwordEncoder;
    @Mock private TokenGeneratorPort tokenGenerator;

    // ── Subject under test ────────────────────────────────────────────────────
    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCase(
            loadUserPort, passwordEncoder, tokenGenerator, new CredentialsVerifier());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private UserAggregate buildUser() {
        return UserAggregate.create(
            Username.of("johndoe"), Email.of(EMAIL), HashedPassword.fromHash(HASHED_PASSWORD));
    }

    private void givenTokensAreStubbed() {
        given(tokenGenerator.generateAccessToken(any(), any())).willReturn("access-token");
        given(tokenGenerator.generateRefreshToken(any())).willReturn("refresh-token");
    }

    // ── Tests ─────────────────────────────────────────────────────────────────
    @Test
    void shouldLoginSuccessfully() {
        // Given
        given(loadUserPort.findByEmail(any(Email.class))).willReturn(Optional.of(buildUser()));
        given(passwordEncoder.matches("password123", HASHED_PASSWORD)).willReturn(true);
        givenTokensAreStubbed();

        // When
        AuthTokenResponse response = loginUseCase.execute(new LoginCommand(EMAIL, "password123"));

        // Then
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        then(loadUserPort).should().findByEmail(any(Email.class));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        // Given
        given(loadUserPort.findByEmail(any(Email.class))).willReturn(Optional.empty());

        // When & Then
        assertThrows(
            InvalidCredentialsException.class,
            () -> loginUseCase.execute(new LoginCommand("nobody@example.com", "password123")));
    }
}
```

### Mapeo BDDMockito ↔ Mockito clásico

| Mockito clásico | BDDMockito equivalente |
|---|---|
| `when(mock.m()).thenReturn(v)` | `given(mock.m()).willReturn(v)` |
| `when(mock.m()).thenAnswer(fn)` | `given(mock.m()).willAnswer(fn)` |
| `when(mock.m()).thenThrow(ex)` | `given(mock.m()).willThrow(ex)` |
| `doNothing().when(mock).m()` | `willDoNothing().given(mock).m()` |
| `doThrow(ex).when(mock).m()` | `willThrow(ex).given(mock).m()` |
| `verify(mock).m()` | `then(mock).should().m()` |
| `verify(mock, never()).m()` | `then(mock).should(never()).m()` |
| `verifyNoMoreInteractions(mock)` | `then(mock).shouldHaveNoMoreInteractions()` |

---

## 6. Patrones de estructura

### Constantes de clase
Extraer todos los strings repetidos como constantes estáticas:

```java
private static final String USER_ID  = "550e8400-e29b-41d4-a716-446655440000";
private static final String EMAIL    = "john@example.com";
private static final String PASSWORD = "SecureP@ss123";
```

### Helper `buildXxx()`
Extraer la construcción de objetos de dominio en un método privado:

```java
private UserAggregate buildUser() { … }
private UserAggregate buildUser(boolean active, boolean emailVerified) { … }  // parametrizado
private JobAggregate createOpenJob() { … }
```

### Helper `givenXxxAreStubbed()`
Extraer stubs que se repiten en múltiples tests:

```java
private void givenTokensAreStubbed() {
    given(tokenGenerator.generateAccessToken(any(), any())).willReturn("access-token");
    given(tokenGenerator.generateRefreshToken(any())).willReturn("refresh-token");
}
```

### `@BeforeEach` para el comando compartido
Si el mismo comando se usa en todos los tests, inicializarlo en `setUp()`:

```java
private VerifyEmailCommand command;

@BeforeEach
void setUp() {
    command = new VerifyEmailCommand(TEST_USER_ID, "verification-token");
}
```

---

## 7. Convenciones de nombres

| Elemento | Convención | Ejemplo |
|---|---|---|
| Clase de test | `<ClaseTesteada>Test` | `UserAggregateTest` |
| Método de test | `should<ComportamientoEsperado>` | `shouldActivateUser` |
| Test de excepción | `shouldThrow<Excepción>When<Condición>` | `shouldThrowWhenEmailAlreadyExists` |
| Helper de construcción | `buildXxx()` / `createXxx()` | `buildUser()`, `createOpenJob()` |
| Helper de stubs | `givenXxxAreStubbed()` | `givenTokensAreStubbed()` |
| Constantes | `UPPER_SNAKE_CASE` | `USER_ID`, `HASHED_PASSWORD` |

---

## 8. Anti-patrones a evitar

### ❌ Imports wildcard
```java
import static org.junit.jupiter.api.Assertions.*;   // ❌
import static org.mockito.Mockito.*;                 // ❌
```
```java
import static org.junit.jupiter.api.Assertions.assertEquals;  // ✅
import static org.mockito.BDDMockito.given;                   // ✅
```

### ❌ Strings literales repetidos
```java
when(port.findByEmail(Email.of("john@example.com"))).thenReturn(...);  // ❌
verify(port).findByEmail(Email.of("john@example.com"));
```
```java
given(port.findByEmail(Email.of(EMAIL))).willReturn(...);  // ✅
then(port).should().findByEmail(Email.of(EMAIL));
```

### ❌ Construcción de dominio duplicada inline
```java
// ❌ — repetido en 3 tests
UserAggregate user = UserAggregate.create(Username.of("johndoe"), Email.of("john@example.com"), ...);
```
```java
// ✅ — helper reutilizable
private UserAggregate buildUser() { return UserAggregate.create(...); }
```

### ❌ Mock innecesario
```java
@Mock private SaveUserPort saveUserPort;  // ❌ si el use case no lo inyecta
```
Declarar `@Mock` solo para dependencias que el use case realmente recibe en su constructor.

### ❌ Sin estructura Given/When/Then
```java
@Test
void shouldLoginSuccessfully() {
    when(port.findByEmail(any())).thenReturn(Optional.of(user));   // ❌ sin secciones
    AuthTokenResponse r = useCase.execute(command);
    assertNotNull(r.accessToken());
}
```

### ❌ Mezclar `when/thenReturn` con `given/willReturn`
Usar **solo** la API BDDMockito en todo el archivo. No mezclar estilos.

---

## 9. Referencia rápida de BDDMockito

> Todos los métodos pertenecen a `org.mockito.BDDMockito`, incluido en `mockito-core`.

### Patrón A — `given().will…()` (métodos con retorno)

```java
// Retornar un valor fijo
given(mock.method(arg)).willReturn(value);

// Retornar con lógica dinámica (p.ej., devolver el argumento recibido)
given(mock.save(any())).willAnswer(invocation -> invocation.getArgument(0));

// Lanzar excepción
given(mock.method(arg)).willThrow(new SomeException());
```

### Patrón B — `will…().given()` (métodos void)

```java
// No-op (comportamiento por defecto en mocks, pero se puede hacer explícito)
willDoNothing().given(mock).publishAll(any());

// Lanzar excepción en método void
willThrow(new RuntimeException()).given(mock).method();

// Lógica dinámica en método void
willAnswer(invocation -> { /* side effect */ return null; }).given(mock).method();
```

### Verificación con `then().should()`

```java
// Se llamó exactamente una vez
then(mock).should().method(arg);

// Nunca se llamó
then(mock).should(never()).method(arg);

// Se llamó N veces
then(mock).should(times(2)).method(arg);

// No hubo más interacciones después de las verificadas
then(mock).shouldHaveNoMoreInteractions();
```

