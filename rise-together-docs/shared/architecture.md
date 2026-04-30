# Arquitectura — Rise Together

## Profiles de Spring Boot

| Profile | Propósito        | DB                        | Credenciales                                 | Logging |
|---------|------------------|---------------------------|----------------------------------------------|---------|
| `dev`   | Desarrollo local | localhost:3306 (Docker)   | Hardcodeadas en `application-dev.properties` | DEBUG   |
| `prod`  | Producción       | Env vars (Docker Compose) | Env vars                                     | WARN    |

### Comportamiento por defecto

- `spring.profiles.active=dev` en `application.properties` base
- Si no se especifica profile, usa `dev`
- Docker Compose inyecta `SPRING_PROFILES_ACTIVE: prod`

---

## Entornos de despliegue

### Desarrollo (local)

```bash
# Terminal 1: Solo la base de datos
docker compose up db

# Terminal 2 o STS: La app (profile dev por defecto)
./mvnw spring-boot:run
```

- MySQL en contenedor Docker (puerto 3306)
- App corriendo localmente desde STS o terminal
- Logs visibles en IDE/terminal
- Hot-reload con DevTools

### Producción

```bash
cp .env.example .env
docker compose up --build
```

- MySQL + App en contenedores Docker
- Variables de entorno inyectadas por Docker Compose
- Logs con `docker compose logs app`
- Profile `prod` configurado automáticamente

---

## Configuración de entorno

### Variables de entorno requeridas

| Variable              | Descripción                            | Ejemplo                           |
|-----------------------|----------------------------------------|-----------------------------------|
| `MYSQL_ROOT_PASSWORD` | Password root de MySQL                 | `rootpass`                        |
| `MYSQL_DATABASE`      | Nombre de la base de datos             | `itjobs`                          |
| `DB_URL`              | URL JDBC completa                      | `jdbc:mysql://db:3306/itjobs?...` |
| `DB_USERNAME`         | Usuario de la DB                       | `root`                            |
| `DB_PASSWORD`         | Password de la DB                      | (igual a `MYSQL_ROOT_PASSWORD`)   |
| `JWT_SECRET`          | Secreto para firmar JWT (min 256 bits) | Ver `.env.example`                |

### Archivos de configuración

```
src/main/resources/
├── application.properties          # Config común + profile default=dev
├── application-dev.properties      # Dev: localhost, credenciales hardcodeadas
└── application-prod.properties     # Prod: env vars inyectadas por Docker
```

---

## Docker Compose

### Servicios

| Servicio | Imagen                   | Puerto | Healthcheck             |
|----------|--------------------------|--------|-------------------------|
| `db`     | mysql:8.0                | 3306   | `mysqladmin ping`       |
| `app`    | Build local (Dockerfile) | 9090   | depende de `db` healthy |

### Características

- MySQL con charset `utf8mb4_unicode_ci`
- Volúmen persistente para datos MySQL (`mysql-data`)
- App espera a que MySQL esté healthy antes de arrancar
- Flyway corre automáticamente al iniciar la app

---

## Charset y UUID

### Problema conocido

Hibernate 6 envía UUID como bytes binarios por defecto. MySQL rechaza estos bytes si el charset no es compatible.

### Solución aplicada

```java

@Id
@JdbcTypeCode(SqlTypes.VARCHAR)     // Fuerza envío como string (36 chars)
@Column(columnDefinition = "CHAR(36)")
private UUID id;
```

Aplicado en: `UserEntity`, `JobEntity`

### Configuración JDBC

```
useUnicode=true&characterEncoding=utf8
```

Agregado a URLs de `application-dev.properties` y `docker-compose.yml`.

---

## Email — Configuración condicional

### Problema

`SmtpEmailSenderAdapter` depende de `JavaMailSender`. Spring Boot solo crea ese
bean si `spring.mail.host` está definido. Sin esa propiedad la app no arrancaba
(ni en dev local sin SMTP, ni en tests).

### Solución: polimorfismo por configuración

Dos implementaciones de `EmailSenderPort`, cada una con su condición:

| Adapter | Condición | Comportamiento |
|---|---|---|
| `SmtpEmailSenderAdapter` | `@ConditionalOnProperty(prefix="spring.mail", name="host")` | Envía email real via `JavaMailSender` |
| `NoOpEmailSenderAdapter` | `@ConditionalOnMissingBean(EmailSenderPort.class)` | Loguea `WARN` y descarta el email |

```java
// Solo se crea si spring.mail.host está definido
@Repository
@ConditionalOnProperty(prefix = "spring.mail", name = "host")
public class SmtpEmailSenderAdapter implements EmailSenderPort { … }

// Fallback: se crea cuando no existe ningún otro EmailSenderPort
@Repository
@ConditionalOnMissingBean(EmailSenderPort.class)
public class NoOpEmailSenderAdapter implements EmailSenderPort { … }
```

### Por qué funciona

Spring evalúa todas las condiciones `@Conditional*` **antes** de instanciar beans:

1. **Sin `spring.mail.host`** → `SmtpEmailSenderAdapter` no se registra →
   `NoOpEmailSenderAdapter` sí se registra (no hay otro `EmailSenderPort`)
2. **Con `spring.mail.host`** → `SmtpEmailSenderAdapter` se registra →
   `NoOpEmailSenderAdapter` no se registra (ya existe un `EmailSenderPort`)

### Configurar SMTP (dev / prod)

```properties
# application-dev.properties  o  application-prod.properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=noreply@itjobs.com
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

Sin esas propiedades (tests, dev sin SMTP), `NoOpEmailSenderAdapter` actúa como
fallback sin romper el contexto de Spring. No se necesitan mocks ni configuraciones
especiales en los tests.

### Patrón aplicado

**Polimorfismo por configuración** en Arquitectura Hexagonal:

- El dominio usa `EmailSenderPort` (interfaz pura)
- La infraestructura provee dos implementaciones
- El entorno (propiedades) elige el comportamiento en tiempo de arranque

---

## CQRS en el bounded context `authentication`

### Motivación

`UserAggregate` es el modelo de escritura: tiene invariantes, listas de eventos,
validación y comportamiento. Instanciarlo solo para leer un email y contraseña es
innecesariamente costoso e introduce lógica de dominio donde no corresponde.

El patrón **CQRS** (Command Query Responsibility Segregation) resuelve esto
separando las rutas de lectura y escritura en dos puertos de salida distintos.

### Los dos puertos de salida de usuario

```
                    ┌─────────────────────────────────────┐
                    │         Use Case (Application)      │
                    └──────┬────────────────────┬─────────┘
                           │                    │
               mutates?    │ yes                │ no (read-only)
                           ▼                    ▼
                   ┌───────────────┐   ┌────────────────────┐
                   │ LoadUserPort  │   │   QueryUserPort     │
                   │  (command)    │   │     (query)         │
                   └───────┬───────┘   └────────┬───────────┘
                           │                    │
                           ▼                    ▼
                   UserAggregate           UserView
                   (write model)          (read model)
```

#### `LoadUserPort` — lado comando

```java
public interface LoadUserPort {
    Optional<UserAggregate> findByEmail(Email email);
    Optional<UserAggregate> findById(UserId id);
}
```

- Devuelve el **modelo de escritura** (`UserAggregate`)
- Solo lo usan use cases que van a **mutar** el agregado
- Implementado por `LoadUserPortAdapter` → `UserReaderRepository` → `JpaUserRepositoryAdapter`

#### `QueryUserPort` — lado query

```java
public interface QueryUserPort {
    Optional<UserView> findByEmail(Email email);
    Optional<UserView> findById(UserId id);
    boolean existsByEmail(Email email);                          // EXISTS sin hidratar entidad
    List<UserId> findUnverifiedUserIdsBefore(Instant cutoff);    // solo IDs para batch
}
```

- Devuelve el **modelo de lectura** (`UserView`, un `record`)
- Solo lo usan use cases de **solo lectura** (login, refresh, forgot, register check, cleanup)
- Implementado por `QueryUserPortAdapter` → `SpringDataJpaUserRepository` directo
- Mapea `UserEntity → UserView` **sin pasar por `UserAggregate`** (no hay reconstitución)

### `UserView` — el read model

```java
public record UserView(
    UserId id,
    String username,
    String email,
    HashedPassword hashedPassword,
    boolean active,
    boolean emailVerified,
    List<String> roles) {}
```

- `record` inmutable: sin lógica de dominio, sin eventos, sin invariantes
- Se construye directamente desde `UserEntity` en el adaptador
- Solo contiene los campos que los use cases de lectura necesitan

### Mapping use case → puerto

| Use Case | Side | Puerto de usuario | Motivo |
|---|---|---|---|
| `LoginUseCase` | Read | `QueryUserPort` | Solo verifica credenciales, no muta |
| `RefreshTokenUseCase` | Read | `QueryUserPort` | Solo valida que el usuario existe y está activo |
| `ForgotPasswordUseCase` | Read | `QueryUserPort` | Solo busca el email, el evento lo maneja el listener |
| `RegisterUserUseCase` | Write | `QueryUserPort` + `SaveUserPort` | `QueryUserPort.existsByEmail()` para check, luego `SaveUserPort` para persistir nuevo `UserAggregate` |
| `DeleteExpiredUnverifiedUsersUseCase` | Write | `QueryUserPort` + `DeleteUserPort` | `QueryUserPort.findUnverifiedUserIdsBefore()` para IDs, `DeleteUserPort` para eliminar |
| `VerifyEmailUseCase` | Write | `LoadUserPort` + `SaveUserPort` | Carga el agregado, llama `verifyEmail()`, guarda |
| `ChangePasswordUseCase` | Write | `LoadUserPort` + `SaveUserPort` | Carga el agregado, llama `changePassword()`, guarda |
| `GoogleAuthUseCase` | Write | `LoadUserPort` + `SaveUserPort` | Busca o crea el agregado, llama `linkGoogleAccount()`, guarda |
| `ResendVerificationUseCase` | Write | `LoadUserPort` + `SaveUserPort` | Carga el agregado, regenera token, guarda |

### Repositorios de dominio (también divididos)

Los repositorios del dominio siguen el mismo patrón por SRP:

```java
// Solo lectura — rehydrata UserAggregate por clave natural
interface UserReaderRepository {
    Optional<UserAggregate> findById(UserId id);
    Optional<UserAggregate> findByEmail(Email email);
    Optional<UserAggregate> findByUsername(Username username);
}

// Solo escritura
interface UserWriterRepository {
    UserAggregate save(UserAggregate user);
}

// Composite (extiende ambos, para casos que necesitan los dos)
interface UserRepository extends UserReaderRepository, UserWriterRepository {}
```

> **Regla**: los use cases inyectan el puerto más específico que necesiten.
> Si solo leen → `LoadUserPort`. Si solo mutan → `SaveUserPort`.
> `UserRepository` queda para adaptadores de infraestructura que necesitan ambos.

### Por qué no un único `UserPort` con todo

Un puerto único `findByEmail(): UserAggregate` haría que:
- `LoginUseCase` instancie `UserAggregate` (invariantes, lista de eventos) solo para leer un password — **costoso e incorrecto semánticamente**
- Sea imposible optimizar la query de lectura (p.ej. `SELECT` de columnas específicas)
- El dominio quede expuesto a operaciones que no le corresponden a la capa de query

Con CQRS:
- ✅ El read model es una proyección plana y barata
- ✅ La query side puede optimizarse independientemente (EXISTS, proyecciones parciales)
- ✅ El write model conserva toda su riqueza sin contaminar la lectura

---
