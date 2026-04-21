# Testing Guide — ITJobsBackend

## Índice

1. [Estrategia general](#1-estrategia-general)
2. [Tipos de tests](#2-tipos-de-tests)
3. [BDD Style: Given / When / Then](#3-bdd-style-given--when--then)
4. [Tests de dominio (sin Spring)](#4-tests-de-dominio-sin-spring)
5. [Tests de casos de uso (con Mockito BDD)](#5-tests-de-casos-de-uso-con-mockito-bdd)
6. [Patrones de estructura](#6-patrones-de-estructura)
7. [Buenas prácticas adicionales con Mockito BDD](#7-buenas-prácticas-adicionales-con-mockito-bdd)
8. [Convenciones de nombres](#8-convenciones-de-nombres)
9. [Anti-patrones a evitar](#9-anti-patrones-a-evitar)
10. [Referencia rápida de BDDMockito](#10-referencia-rápida-de-bddmockito)

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

El subject under test se declara con `@InjectMocks` — Mockito lo instancia
automáticamente inyectando todos los campos `@Mock` y `@Spy` que coincidan
con el constructor del use case.

```java
@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    // ── Constants ─────────────────────────────────────────────────────────────
    private static final String EMAIL           = "john@example.com";
    private static final String HASHED_PASSWORD = "$2a$10$hashed";

    // ── Mocks (solo puertos de salida) ────────────────────────────────────────
    @Mock private LoadUserPort       loadUserPort;
    @Mock private PasswordEncoderPort passwordEncoder;
    @Mock private TokenGeneratorPort  tokenGenerator;

    // ── Subject under test ────────────────────────────────────────────────────
    @InjectMocks private LoginUseCase loginUseCase;

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

### `@InjectMocks` con domain services

Cuando el use case depende de un **domain service** (p.ej. `CredentialsVerifier`) que no
es un puerto y **no debe mockearse**, decláralo con `@Spy` para que Mockito lo instancie
como objeto real y lo inyecte junto con los `@Mock`:

```java
// ── Domain services (instancia real, no mock) ─────────────────────────────
@Spy private CredentialsVerifier credentialsVerifier;

// ── Subject under test ────────────────────────────────────────────────────
@InjectMocks private LoginUseCase loginUseCase;
// Mockito inyecta: loadUserPort (@Mock) + passwordEncoder (@Mock)
//                + tokenGenerator (@Mock) + credentialsVerifier (@Spy)
```

> **Regla:** `@Spy` es para clases de dominio sin estado o con lógica pura que
> necesitas que se ejecute de verdad. Nunca uses `@Mock` para un domain service,
> aggregate o value object.

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

### `@InjectMocks` para el subject under test

El use case **nunca** se instancia manualmente en `@BeforeEach`.
Siempre se declara con `@InjectMocks`:

```java
// ✅
@InjectMocks private RegisterUserUseCase registerUserUseCase;

// ❌ — manual, error-prone, oculta dependencias
private RegisterUserUseCase registerUserUseCase;
@BeforeEach void setUp() { registerUserUseCase = new RegisterUserUseCase(...); }
```

Mockito infiere el constructor más largo que pueda satisfacer con los `@Mock` y `@Spy`
declarados en la misma clase de test.

### `@BeforeEach` para el comando compartido

`@BeforeEach` se usa únicamente para inicializar **comandos, queries u otros DTOs**
que se repiten en todos los tests del archivo:

```java
private VerifyEmailCommand command;

@BeforeEach
void setUp() {
    command = new VerifyEmailCommand(TEST_USER_ID, "verification-token");
}
```

---

## 7. Buenas prácticas adicionales con Mockito BDD

### Capturar argumentos con `ArgumentCaptor`

Cuando necesitas verificar el **estado real del objeto** que se envía a un puerto, no uses
`any()` ni `argThat()` con lógica embebida. La forma más clara y mantenible es usar
`ArgumentCaptor`: permite capturar el objeto que el use case construyó y hacer assertions
sobre él después de la ejecución.

```java
import org.mockito.ArgumentCaptor;
```

#### Ejemplo

```java
@Test
void shouldDeriveUsernameFromEmailWhenNameIsNull() {
    // Given
    GoogleAuthCommand command = new GoogleAuthCommand(GOOGLE_SUB, EMAIL, null, true);

    given(loadUserPort.findByEmail(Email.of(EMAIL))).willReturn(Optional.empty());
    given(saveUserPort.save(any(UserAggregate.class)))
        .willAnswer(invocation -> invocation.getArgument(0));
    givenTokensAreStubbed();
    givenTermsDocumentExists();

    // When
    useCase.execute(command);

    // Then
    ArgumentCaptor<UserAggregate> captor = ArgumentCaptor.forClass(UserAggregate.class);
    then(saveUserPort).should().save(captor.capture());

    assertEquals("john", captor.getValue().getUsername().value());
}
```

#### ¿Por qué usar `ArgumentCaptor`?

- Inspecciona el objeto **real** construido por el use case, no una aproximación
- Evita lógica compleja dentro de `argThat`
- Mantiene los assertions claros y en su sección `// Then`

```java
// ❌ Difícil de leer
then(repo).should().save(argThat(u -> u.getUsername().value().equals("john")));

// ✅ Claro y mantenible
ArgumentCaptor<UserAggregate> captor = ArgumentCaptor.forClass(UserAggregate.class);
then(repo).should().save(captor.capture());
assertEquals("john", captor.getValue().getUsername().value());
```

> **Regla:** `ArgumentCaptor` siempre va dentro de `// Then`, justo antes del `assertEquals`.
> Nunca lo declares en `// Given` ni lo captures en `// When`.

---

### Cuándo usar `argThat` (y cuándo no)

`argThat` es un matcher inline que acepta una lambda de verificación. Es útil en casos
muy concretos, pero se vuelve ilegible si la condición es compleja.

```java
import static org.mockito.ArgumentMatchers.argThat;
```

#### ✅ Casos donde `argThat` es apropiado

**1. Verificación de un único campo simple en un stub del `// Given`**

Cuando necesitas que el stub reaccione solo si el argumento cumple una condición
mínima, y no te importa hacer ningún assertion posterior sobre él:

```java
// Given — solo activar el stub si el email es exactamente el correcto
given(loadUserPort.findByEmail(argThat(e -> e.value().equals(EMAIL))))
    .willReturn(Optional.of(buildUser()));
```

**2. Verificar la llamada y una única propiedad a la vez en `// Then`**

Cuando solo necesitas confirmar un campo sin hacer varios `assertEquals`, y la
condición cabe en una línea corta:

```java
// Then — confirmar que se guardó un usuario con el email correcto
then(saveUserPort).should().save(argThat(u -> u.getEmail().value().equals(EMAIL)));
```

**3. Verificar con un tipo específico sin captura previa**

Cuando solo te interesa el tipo concreto del argumento y no su estado interno:

```java
// Then — confirmar que el evento publicado es del tipo correcto
then(eventPublisher).should().publish(argThat(e -> e instanceof UserRegisteredEvent));
```

#### ❌ Cuándo NO usar `argThat` — usa `ArgumentCaptor` en su lugar

| Situación | Por qué evitar `argThat` |
|---|---|
| Verificar más de un campo | La lambda crece y pierde legibilidad |
| El mensaje de fallo debe ser descriptivo | `argThat` solo dice "no coincidió", sin detalle |
| Quieres reutilizar el objeto capturado | `argThat` no da acceso al objeto tras la verificación |
| Necesitas varios `assertEquals` sobre el mismo objeto | `ArgumentCaptor` es más claro y fácil de depurar |

```java
// ❌ — múltiples condiciones en argThat
then(saveUserPort).should().save(argThat(u ->
    u.getEmail().value().equals(EMAIL) &&
    u.getUsername().value().equals("john") &&
    !u.isActive()));

// ✅ — misma verificación con ArgumentCaptor: cada condición es un assertEquals
ArgumentCaptor<UserAggregate> captor = ArgumentCaptor.forClass(UserAggregate.class);
then(saveUserPort).should().save(captor.capture());
UserAggregate saved = captor.getValue();
assertEquals(EMAIL,  saved.getEmail().value());
assertEquals("john", saved.getUsername().value());
assertFalse(saved.isActive());
```

#### Tabla de decisión: `argThat` vs `ArgumentCaptor`

| Criterio | `argThat` | `ArgumentCaptor` |
|---|---|---|
| Una sola condición simple | ✅ | también válido |
| Múltiples condiciones | ❌ | ✅ |
| Mensaje de fallo descriptivo | ❌ | ✅ |
| Reutilizar el objeto capturado | ❌ | ✅ |
| Stub en `// Given` con condición mínima | ✅ | innecesario |

---

### Simular repositorios con `willAnswer`

Cuando un puerto (p.ej. `save`) devuelve la misma entidad que recibe, usar
`willReturn` no es posible porque el objeto aún no existe al momento de declarar el stub.
La solución es `willAnswer`:

```java
given(saveUserPort.save(any(UserAggregate.class)))
    .willAnswer(invocation -> invocation.getArgument(0));
```

`invocation.getArgument(0)` devuelve el **primer parámetro** recibido en la llamada real.

Esto permite que el use case haga:

```java
UserAggregate savedUser = saveUserPort.save(user);
```

…y el test siga funcionando sin crear objetos falsos manualmente.

#### Cuándo usar `willAnswer`

| Caso | Ejemplo |
|---|---|
| El método devuelve el mismo objeto que recibe | `repository.save(entity)` |
| El resultado depende del argumento de entrada | `hashPassword(rawPassword)` |
| Necesitas lógica dinámica basada en el input | cálculos en base al argumento |

No lo uses cuando basta con `willReturn(valorFijo)`.

#### Tabla de decisión

| Situación | Herramienta |
|---|---|
| Solo verificar que se llamó | `then(mock).should()` |
| Verificar los valores del argumento | `ArgumentCaptor` |
| Simular comportamiento dinámico | `willAnswer` |
| Simular retorno simple y fijo | `willReturn` |

#### Ejemplo completo (patrón recomendado)

```java
@Test
void shouldCreateUser() {
    // Given
    given(loadUserPort.findByEmail(any(Email.class))).willReturn(Optional.empty());
    given(saveUserPort.save(any(UserAggregate.class)))
        .willAnswer(invocation -> invocation.getArgument(0));

    // When
    useCase.execute(command);

    // Then
    ArgumentCaptor<UserAggregate> captor = ArgumentCaptor.forClass(UserAggregate.class);
    then(saveUserPort).should().save(captor.capture());
    assertEquals("john", captor.getValue().getUsername().value());
}
```

---

## 8. Convenciones de nombres

| Elemento | Convención | Ejemplo |
|---|---|---|
| Clase de test | `<ClaseTesteada>Test` | `UserAggregateTest` |
| Método de test | `should<ComportamientoEsperado>` | `shouldActivateUser` |
| Test de excepción | `shouldThrow<Excepción>When<Condición>` | `shouldThrowWhenEmailAlreadyExists` |
| Helper de construcción | `buildXxx()` / `createXxx()` | `buildUser()`, `createOpenJob()` |
| Helper de stubs | `givenXxxAreStubbed()` | `givenTokensAreStubbed()` |
| Constantes | `UPPER_SNAKE_CASE` | `USER_ID`, `HASHED_PASSWORD` |
| Subject under test | `@InjectMocks` | `@InjectMocks private LoginUseCase loginUseCase;` |
| Puertos mockeados | `@Mock` | `@Mock private LoadUserPort loadUserPort;` |
| Domain services | `@Spy` | `@Spy private CredentialsVerifier credentialsVerifier;` |

---

## 9. Anti-patrones a evitar

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

### ❌ Mockear clases de dominio

Nunca usar `@Mock` sobre un aggregate, value object o domain service.
Estas clases contienen la lógica de negocio y deben ejecutarse como código real.

```java
@Mock private UserAggregate user;         // ❌ — aggregate
@Mock private Email email;                // ❌ — value object
@Mock private CredentialsVerifier cv;     // ❌ — domain service
```
```java
// ✅ — instancia real en el helper
private UserAggregate buildUser() { return UserAggregate.create(...); }

// ✅ — domain service como @Spy (se ejecuta de verdad, Mockito lo inyecta)
@Spy private CredentialsVerifier credentialsVerifier;
```

Solo deben mockearse los **puertos de salida** (interfaces que el use case recibe
por inyección de dependencias):

```java
@Mock private LoadUserPort   loadUserPort;   // ✅ — puerto de salida
@Mock private SaveUserPort   saveUserPort;   // ✅ — puerto de salida
@Mock private TokenGeneratorPort tokenGen;   // ✅ — puerto de salida
```

### ❌ Instanciar el subject manualmente

```java
// ❌ — construcción manual en @BeforeEach
private LoginUseCase loginUseCase;
@BeforeEach
void setUp() {
    loginUseCase = new LoginUseCase(loadUserPort, passwordEncoder, tokenGenerator, new CredentialsVerifier());
}
```
```java
// ✅ — Mockito gestiona la instanciación
@Spy   private CredentialsVerifier  credentialsVerifier;
@InjectMocks private LoginUseCase loginUseCase;
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

## 10. Referencia rápida de BDDMockito

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

