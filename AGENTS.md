# AGENTS.md - ITJobsBackend

## Build & Test Commands

```bash
# Build the project
./mvnw clean package

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=UserAggregateTest

# Run a single test method
./mvnw test -Dtest=UserAggregateTest#shouldActivateUser

# Run the application locally (requires MySQL)
./mvnw spring-boot:run

```

> **Code style**: See [`itjobs-docs/code-style.md`](itjobs-docs/code-style.md) for Google Java Format setup
> and IntelliJ configuration (JRE exports requeridos).

---

## Architecture

This project follows **Hexagonal Architecture (Ports and Adapters)** with **Domain-Driven Design (DDD) tactical patterns**:

- **Hexagonal Architecture**: defines where things live (ports, adapters, bounded contexts)
- **DDD Tactical Patterns**: defines what things are (aggregates, value objects, domain events, etc.)

> 📖 **Onboarding**: Para una guía completa de arquitectura hexagonal con ejemplos del proyecto, ver [`itjobs-docs/hexagonal-onboarding.md`](itjobs-docs/hexagonal-onboarding.md)

### Bounded Contexts

- `authentication` - User registration, login, credentials, Google OAuth, email verification, password reset
- `jobs` - Job listings, search, filtering
- `shared` - Shared kernel (base exceptions, value objects, events)
- `applications` - Job applications, pipeline, saved jobs *(DB created, pending implementation)*
- `profiles` - Candidate/employer/recruiter profiles *(DB created, pending implementation)*
- `matching` - Skill catalog, synonyms, relationships, match scoring *(planned — see requirements-v2.md)*
- `moderation` - Reports, decisions, appeals, content moderation *(planned — see requirements-v2.md)*
- `billing` - Subscription plans, payments, feature limits *(planned — see requirements-v2.md)*
- `analytics` - Job views, conversion metrics, dashboards *(planned — see requirements-v2.md)*
- `administration` - User/role/category management, audit log, onboarding flows, help center

### Project Tree

```
src/main/java/com/ITJobsBackend/
├── authentication/
│   ├── application/
│   │   ├── ports/in/
│   │   │   ├── ChangePasswordPort.java
│   │   │   ├── DeleteExpiredUnverifiedUsersPort.java
│   │   │   ├── ForgotPasswordPort.java
│   │   │   ├── GoogleAuthPort.java
│   │   │   ├── LoginPort.java
│   │   │   ├── RefreshTokenPort.java
│   │   │   ├── RegisterUserPort.java
│   │   │   ├── ResendVerificationPort.java
│   │   │   └── VerifyEmailPort.java
│   │   ├── ports/out/
│   │   │   ├── DeleteUserPort.java
│   │   │   ├── EmailSenderPort.java
│   │   │   ├── LoadTermsDocumentPort.java
│   │   │   ├── LoadUserPort.java          ← command side: returns UserAggregate
│   │   │   ├── PasswordEncoderPort.java
│   │   │   ├── QueryUserPort.java         ← query side:   returns UserView (projection)
│   │   │   ├── SaveTermsAcceptancePort.java
│   │   │   ├── SaveUserPort.java
│   │   │   ├── TokenGeneratorPort.java
│   │   │   └── VerificationTokenValidatorPort.java
│   │   ├── query/
│   │   │   └── UserView.java              ← read model (projection), record
│   │   └── usecases/
│   │       ├── changepassword/ ChangePasswordCommand, ChangePasswordUseCase
│   │       ├── deleteexpiredunverifiedusers/ DeleteExpiredUnverifiedUsersCommand, DeleteExpiredUnverifiedUsersUseCase
│   │       ├── forgot/     ForgotPasswordCommand, ForgotPasswordUseCase
│   │       ├── google/     GoogleAuthCommand, GoogleAuthUseCase
│   │       ├── login/      LoginCommand, LoginUseCase, AuthTokenResponse
│   │       ├── refresh/    RefreshTokenUseCase
│   │       ├── register/   RegisterUserCommand, RegisterUserUseCase, RegisterUserResponse
│   │       ├── resendverification/ ResendVerificationCommand, ResendVerificationUseCase
│   │       └── verify/     VerifyEmailCommand, VerifyEmailUseCase
│   ├── domain/
│   │   ├── aggregate/      UserAggregate, TermsDocument
│   │   ├── entity/         TermsAcceptance
│   │   ├── event/          UserRegisteredEvent, UserActivatedEvent, UserDeactivatedEvent,
│   │   │                   EmailVerifiedEvent, PasswordResetRequestedEvent,
│   │   │                   EmailChangedEvent, GoogleAccountLinkedEvent,
│   │   │                   PasswordChangedEvent, UserEventTypes
│   │   ├── exceptions/     UserAlreadyExistsException, UserAlreadyActivatedException,
│   │   │                   UserAlreadyDeactivatedException, EmailAlreadyVerifiedException,
│   │   │                   InvalidCredentialsException, VerificationTokenExpiredException,
│   │   │                   TermsDocumentNotFoundException
│   │   ├── repository/     UserRepository, UserReaderRepository, UserWriterRepository,
│   │   │                   TermsDocumentReaderRepository, TermsAcceptanceWriterRepository
│   │   ├── service/        CredentialsVerifier
│   │   └── valueobjects/   GoogleSub, HashedPassword, Username, VerificationToken,
│   │                       PasswordResetToken, UserStatus,
│   │                       TermsType, TermsDocumentId, TermsAcceptanceId
│   └── infrastructure/
│       ├── adapters/in/rest/   AuthController  (Auth endpoints), AccountController (Account endpoints)
│       │   └── dto/            RegisterRequest, RegisterResponse, LoginRequest, AuthResponse,
│       │                       GoogleLoginRequest, VerifyEmailRequest, ChangePasswordRequest
│       ├── adapters/out/
│       │   ├── email/          SmtpEmailSenderAdapter (@ConditionalOnProperty spring.mail.host),
│       │   │                   NoOpEmailSenderAdapter (fallback — @ConditionalOnMissingBean, WARN y descarta)
│       │   ├── persistence/    LoadUserPortAdapter     (command side → UserAggregate),
│       │   │                   QueryUserPortAdapter    (query side  → UserView),
│       │   │                   SaveUserPortAdapter,
│       │   │                   JpaUserRepositoryAdapter, SpringDataJpaUserRepository,
│       │   │                   UserEntity, UserMapper,
│       │   │                   JpaTermsRepositoryAdapter,
│       │   │                   SpringDataJpaTermsDocumentRepository,
│       │   │                   SpringDataJpaTermsAcceptanceRepository,
│       │   │                   TermsDocumentEntity, TermsAcceptanceEntity,
│       │   │                   TermsDocumentMapper, TermsAcceptanceMapper
│       │   └── security/       BCryptPasswordEncoderAdapter, JwtTokenGeneratorAdapter,
│       │                       JwtAuthenticationFilter, VerificationTokenValidatorAdapter
│       ├── config/             SecurityConfig, DomainServiceConfig
│       └── events/             UserRegisteredEventListener, UserActivatedEventListener,
│                               UserDeactivatedEventListener
├── jobs/
│   ├── application/
│   │   ├── ports/in/       CreateJobPort, SearchJobsPort
│   │   └── usecases/
│   │       ├── createjob/  CreateJobCommand, CreateJobUseCase
│   │       └── searchjobs/ SearchJobsQuery, SearchJobsUseCase, JobResponse
│   ├── domain/
│   │   ├── aggregate/      JobAggregate
│   │   ├── event/          JobCreatedEvent, JobClosedEvent, JobDeactivatedEvent
│   │   ├── exceptions/     JobNotFoundException
│   │   ├── repository/     JobRepository, JobReaderRepository, JobWriterRepository
│   │   ├── specification/  JobSpecification, TitleContainsSpecification,
│   │   │                   CompanySpecification, LocationSpecification, JobStatusSpecification
    └── valueobjects/   JobId, JobStatus, EmploymentType, WorkModality, Salary
│   └── infrastructure/
│       ├── adapters/in/rest/   JobController  (POST /api/v1/jobs, GET /api/v1/jobs?title=)
│       ├── adapters/out/       JpaJobRepositoryAdapter, JobEntity, JobMapper
│       └── events/             JobClosedEventListener, JobDeactivatedEventListener
├── profiles/
│   ├── domain/
│   │   ├── aggregate/      EmployerAggregate
│   │   └── repository/     EmployerReaderRepository, EmployerWriterRepository
│   └── infrastructure/
│       └── adapters/out/persistence/   JpaEmployerRepositoryAdapter,
│                                       SpringDataJpaEmployerRepository,
│                                       EmployerEntity, EmployerMapper
└── shared/
    ├── application/ports/out/  DomainEventPublisher
    ├── domain/
    │   ├── AggregateRoot (abstract)
    │   ├── event/          DomainEvent (abstract)
    │   ├── exceptions/     DomainException (abstract), ValidationException, NotFoundException
    │   └── valueobjects/   Email, EmployerId, Identifier, Password, Timestamp, UserId
    └── infrastructure/
        ├── events/         SpringDomainEventPublisher
        └── exceptions/     GlobalExceptionHandler, SecurityExceptionHandler
```

### Test Tree

```
src/test/java/com/ITJobsBackend/
├── authentication/
│   ├── application/
│   │   └── usecases/
│   │       ├── changepassword/ ChangePasswordUseCaseTest
│   │       ├── deleteexpiredunverifiedusers/ DeleteExpiredUnverifiedUsersUseCaseTest
│   │       ├── forgot/         ForgotPasswordUseCaseTest
│   │       ├── google/         GoogleAuthUseCaseTest
│   │       ├── login/          LoginUseCaseTest
│   │       ├── refresh/        RefreshTokenUseCaseTest
│   │       ├── register/       RegisterUserUseCaseTest
│   │       ├── resendverification/ ResendVerificationUseCaseTest
│   │       └── verify/         VerifyEmailUseCaseTest
│   ├── domain/
│   │   ├── aggregate/          UserAggregateTest
│   │   └── valueobjects/       VerificationTokenTest
│   └── infrastructure/
│       ├── adapters/out/persistence/  LoadUserPortAdapterTest, QueryUserPortAdapterTest
│       └── events/             UserRegisteredEventListenerTest
├── jobs/
│   ├── application/
│   │   └── usecases/
│   │       └── addskills/      AddSkillsUseCaseTest
│   └── domain/
│       ├── aggregate/          JobAggregateTest
│       └── valueobjects/       SalaryTest
├── shared/
│   └── domain/
│       └── valueobjects/       EmailTest, PasswordTest
└── tests/
    └── ITJobsBackendApplicationTests
```

---

## Diagrams

All diagrams are written in **PlantUML** and live in [`itjobs-docs/diagrams/`](itjobs-docs/diagrams/), organized by bounded context.

```
itjobs-docs/diagrams/
├── auth/
│   ├── auth-overview.puml               Component  Authentication: capas hexagonales + CQRS + email adapters
│   ├── class-diagram-auth.puml          Class      UserAggregate, TermsDocument, VOs, eventos, CQRS ports
│   ├── usecases/
│   │   ├── uc-register.puml             Component  RegisterUserUseCase con sus ports (QueryUserPort + ToS)
│   │   ├── uc-login.puml                Component  LoginUseCase con QueryUserPort (CQRS query side)
│   │   ├── uc-google-auth.puml             Component  GoogleAuthUseCase — visión general (todos los ports)
│   │   ├── uc-google-auth-identify.puml    Component  Fase 1 — buscar e identificar al usuario (LoadUserPort)
│   │   ├── uc-google-auth-provision.puml   Component  Fase 2 — crear/vincular usuario + aceptación de términos
│   │   ├── uc-google-auth-tokens.puml      Component  Fase 3 — emitir tokens JWT (TokenGeneratorPort)
│   │   ├── uc-verify-email.puml         Component  VerifyEmailUseCase con sus ports
│   │   ├── uc-change-password.puml      Component  ChangePasswordUseCase con sus ports
│   │   ├── uc-forgot-password.puml      Component  ForgotPasswordUseCase con QueryUserPort (CQRS query)
│   │   ├── uc-resend-verification.puml  Component  ResendVerificationUseCase con EmailSenderPort
│   │   ├── uc-refresh-token.puml        Component  RefreshTokenUseCase con QueryUserPort (CQRS query)
│   │   └── uc-delete-expired-users.puml Component  DeleteExpiredUnverifiedUsersUseCase con DeleteUserPort
│   └── sequences/
│       ├── seq-register.puml            Sequence   Registration flow (con ToS acceptance)
│       ├── seq-login.puml               Sequence   Login flow (CQRS — QueryUserPort → UserView)
│       ├── seq-google-auth.puml         Sequence   Google OAuth login / register flow
│       ├── seq-verify-email.puml        Sequence   Email verification flow
│       ├── seq-change-password.puml     Sequence   Change password flow (authenticated)
│       ├── seq-forgot-password.puml     Sequence   Forgot password flow (CQRS — QueryUserPort)
│       ├── seq-resend-verification.puml Sequence   Resend verification email flow (EmailSenderPort)
│       ├── seq-refresh-token.puml       Sequence   Refresh token flow (CQRS — QueryUserPort)
│       └── seq-delete-expired-users.puml Sequence  Batch delete expired unverified users
├── jobs/
│   ├── class-diagram-jobs.puml     Class      JobAggregate, VOs, eventos, specs, ports
│   ├── seq-apply.puml              Sequence   Job application flow
│   ├── act-job-management.puml     Activity   Job management activity flow
│   ├── state-job.puml              State      Job lifecycle (OPEN → CLOSED / INACTIVE)
│   └── state-application.puml      State      Application lifecycle (PENDING → ACCEPTED / REJECTED)
└── shared/
    ├── class-diagram-shared.puml   Class      Shared Kernel: value objects, DomainEvent, excepciones
    ├── use-cases.puml              Use case   Actors and use cases across all bounded contexts
    ├── er-diagram.puml             ER         All database tables and relationships (mirrors Flyway)
    ├── context-map.puml            Context    High-level bounded context relationships and events
    └── class-diagram.puml          Class      [DEPRECATED] Diagrama consolidado (ver diagramas separados)
```

> Render locally with the [PlantUML IntelliJ plugin](https://plugins.jetbrains.com/plugin/7017-plantuml-integration)
> or online at [plantuml.com/plantuml](https://www.plantuml.com/plantuml).

---

## REST API Endpoints

| Method | Path                     | Use Case              | Auth required |
|--------|--------------------------|-----------------------|---------------|
| POST   | /api/v1/auth/register    | RegisterUserUseCase   | No            |
| POST   | /api/v1/auth/login       | LoginUseCase          | No            |
| POST   | /api/v1/auth/login/google| GoogleAuthUseCase     | No            |
| POST   | /api/v1/auth/verify-email| VerifyEmailUseCase    | No            |
| POST   | /api/v1/account/change-password| ChangePasswordUseCase | Yes     |
| POST   | /api/v1/jobs             | CreateJobUseCase      | No (public)   |
| GET    | /api/v1/jobs?title=      | SearchJobsUseCase     | No (public)   |

> Endpoint for ForgotPassword is not yet wired to a controller.

---

## Domain — authentication

### UserAggregate

| Factory / method       | Description                                              |
|------------------------|----------------------------------------------------------|
| `create()`             | Registers a new user (inactive, email not verified)      |
| `createGoogleUser()`   | Creates user via Google OAuth (active + emailVerified)   |
| `reconstitute()`       | Rebuilds from persistence (no side effects)              |
| `activate()`           | Sets active=true, fires `UserActivatedEvent`             |
| `deactivate()`         | Sets active=false, fires `UserDeactivatedEvent`          |
| `verifyEmail()`        | Sets emailVerified=true, fires `EmailVerifiedEvent`      |
| `changePassword()`     | Replaces hashed password                                 |
| `linkGoogleAccount()`  | Links a `GoogleSub` to an existing user                  |
| `updateEmail()`        | Updates email and resets emailVerified to false          |
| `addRole()`            | Adds a role (normalized, deduped)                        |

> Terms acceptance is no longer on `UserAggregate`. It is recorded as a `TermsAcceptance` entity
> by `RegisterUserUseCase` and `GoogleAuthUseCase` after user creation, referencing a `TermsDocument`.

### TermsDocument

| Factory / method    | Description                                                       |
|---------------------|-------------------------------------------------------------------|
| `create()`          | Publishes a new legal document (type + version + full content)    |
| `reconstitute()`    | Rebuilds from persistence (no side effects)                       |

### TermsAcceptance (entity)

| Factory / method    | Description                                                            |
|---------------------|------------------------------------------------------------------------|
| `create()`          | Records that a user accepted a specific `TermsDocument` at `now()`    |
| `reconstitute()`    | Rebuilds from persistence (no side effects)                            |

### Use Cases

| Use Case               | Port (in)             | Side  | Ports (out)                        | Description                                              |
|------------------------|-----------------------|-------|------------------------------------|----------------------------------------------------------|
| `RegisterUserUseCase`  | `RegisterUserPort`    | Write | `QueryUserPort`, `SaveUserPort`    | Hash password → save → record ToS acceptance → publish `UserRegisteredEvent` |
| `LoginUseCase`         | `LoginPort`           | Read  | `QueryUserPort`                    | Verify credentials → generate JWT tokens                 |
| `RefreshTokenUseCase`  | `RefreshTokenPort`    | Read  | `QueryUserPort`, `TokenGeneratorPort` | Validate refresh token → generate new token pair      |
| `ChangePasswordUseCase`| `ChangePasswordPort`  | Write | `LoadUserPort`, `SaveUserPort`     | Verifies old password → hashes & saves new password      |
| `ForgotPasswordUseCase`| `ForgotPasswordPort`  | Read  | `QueryUserPort`                    | Lookup by email → publish `PasswordResetRequestedEvent`  |
| `GoogleAuthUseCase`    | `GoogleAuthPort`      | Write | `LoadUserPort`, `SaveUserPort`     | Find or create user via Google sub → generate JWT tokens; records ToS acceptance for new users |
| `VerifyEmailUseCase`   | `VerifyEmailPort`     | Write | `LoadUserPort`, `SaveUserPort`     | Lookup by id → `verifyEmail()` → publish `EmailVerifiedEvent` |
| `ResendVerificationUseCase` | `ResendVerificationPort` | Write | `LoadUserPort`, `SaveUserPort`, `EmailSenderPort` | Regenerate verification token → send email |
| `DeleteExpiredUnverifiedUsersUseCase` | `DeleteExpiredUnverifiedUsersPort` | Write | `QueryUserPort`, `DeleteUserPort` | Batch: find expired unverified user IDs → delete each |

> **CQRS split**: use cases que solo leen datos usan `QueryUserPort` (devuelve `UserView`).
> Use cases que mutan el agregado usan `LoadUserPort` (devuelve `UserAggregate`).
> Ver [`itjobs-docs/architecture.md`](itjobs-docs/architecture.md) — sección *CQRS en el bounded context authentication*.

### Domain Events

| Event                         | Fired by                    |
|-------------------------------|-----------------------------|
| `UserRegisteredEvent`         | `RegisterUserUseCase`       |
| `UserActivatedEvent`          | `UserAggregate.activate()`  |
| `UserDeactivatedEvent`        | `UserAggregate.deactivate()`|
| `EmailVerifiedEvent`          | `UserAggregate.verifyEmail()`|
| `PasswordResetRequestedEvent` | `ForgotPasswordUseCase`     |
| `EmailChangedEvent`           | `UserAggregate.updateEmail()` |
| `GoogleAccountLinkedEvent`    | `UserAggregate.linkGoogleAccount()` |
| `PasswordChangedEvent`        | `UserAggregate.changePassword()` |

### Value Objects (authentication)

| Value Object    | Validation                                     |
|-----------------|------------------------------------------------|
| `Username`      | Not blank, max 50 chars                        |
| `HashedPassword`| Not blank; `fromHash()` skips re-encoding      |
| `GoogleSub`     | Not blank, max 255 chars                       |

---

## Domain — jobs

### JobAggregate

| Factory / method | Description                                   |
|------------------|-----------------------------------------------|
| `create()`       | Creates a new OPEN job, fires `JobCreatedEvent`|
| `close()`        | Sets status CLOSED, fires `JobClosedEvent`    |
| `deactivate()`   | Sets status INACTIVE, fires `JobDeactivatedEvent` |
| `addSkill()`     | Adds a skill (deduped)                        |

### Specifications (Filtering)

| Specification             | Filter                     |
|---------------------------|----------------------------|
| `TitleContainsSpecification` | title LIKE %keyword%    |
| `CompanySpecification`    | exact company match        |
| `LocationSpecification`   | exact location match       |
| `JobStatusSpecification`  | exact status match         |

---

## Database (Flyway Migrations)

| Version | Content                                                  |
|---------|----------------------------------------------------------|
| V1      | `users`, `user_roles`, `jobs`, `job_skills`              |
| V2      | Fix `job_skills` for `@ElementCollection`                |
| V3      | `employers`, `candidates`, `candidate_skills`            |
| V4      | `applications`, `saved_jobs`                             |
| V5      | `job_categories`, `job_category_mapping`                 |
| V6      | `recruiters`                                             |
| V7      | `salary_min` / `salary_max` DOUBLE → DECIMAL(15,2)       |
| V8      | `salary_min` / `salary_max` NOT NULL                     |
| V9      | Add `google_sub VARCHAR(255) UNIQUE` to `users`          |
| V10     | Add `verification_token` and `expires_at` to `users`     |
| V11     | Add `employer_id CHAR(36)` FK to `jobs` → `employers`    |
| V12     | Add `company_size VARCHAR(20)` to `employers`            |
| V13     | Add `logo_url VARCHAR(500)` and `description TEXT` to `employers` |
| V14     | Add `terms_version VARCHAR(10)` and `terms_accepted_at TIMESTAMP` to `users` |
| V15     | Create `terms_documents` (catalog) + `user_terms_acceptances` (audit); migrate V14 data; drop columns from `users` |
| V16     | Add `employer_type VARCHAR(30)` to `employers` |
| V17     | Add `created_by_user_id`, `posted_on_behalf_of_employer_id`, `job_type`, `remote_allowed`, `expires_at`, `featured`, `featured_until` to `jobs` |
| V18     | Create `recruiter_employer_associations` (recruiter ↔ employers many-to-many) |
| V19     | Extend `candidates` with `years_of_experience`, `desired_salary_*`, `desired_employment_type`, `open_to_remote`, `available_for_freelance` |
| V20     | Create `freelancer_profiles` (public freelancer profile linked to user) |
| V21     | Add `work_modality VARCHAR(20)` to `jobs` (`REMOTE`, `HYBRID`, `ON_SITE`) |

---

## Code Style

### Naming Conventions

- **Packages**: lowercase, e.g. `com.ITJobsBackend.authentication.domain.aggregate`
- **Use Cases**: `RegisterUserUseCase`, `SearchJobsUseCase` — verb + noun + "UseCase"
- **Commands/Queries**: `RegisterUserCommand`, `SearchJobsQuery` — immutable records
- **Ports**: `RegisterUserPort`, `SaveUserPort` — interfaces ending in "Port"
- **Adapters**: `JpaUserRepositoryAdapter`, `BCryptPasswordEncoderAdapter` — implementation + "Adapter"
- **Entities**: `JobEntity`, `UserEntity` — noun + "Entity"
- **Aggregates**: `UserAggregate`, `JobAggregate` — noun + "Aggregate"
- **Value Objects**: `Email`, `UserId`, `HashedPassword` — immutable, use factory methods (`Email.of()`)
- **Exceptions**: `UserAlreadyExistsException`, `JobNotFoundException` — descriptive + "Exception"

### Imports

- Orden definido en `.editorconfig` (`ij_java_imports_layout`), aplicado automáticamente con **⌘⌥L**:
  `* → java.** → javax.** → $* (static) → org.junit.** → jakarta.** → lombok.** → org.slf4j.** → org.hibernate.** → org.** → com.**`
- No wildcard imports except `jakarta.persistence.*` in JPA entities
- Static imports allowed for test assertions: `org.junit.jupiter.api.Assertions.*`, `org.mockito.Mockito.*`

### Formatting

- Indentation: 4 spaces (Google Java Format, AOSP style)
- Use tabs in pom.xml, spaces in Java files
- Opening brace on same line
- Constructor injection preferred over field injection
- Single-line getters: `public UserId getId() { return id; }`

### DDD Patterns

- **Value Objects**: Private constructor, static factory method (`of()`, `fromHash()`), validate in factory, override `equals`/`hashCode`
- **Aggregates**: Private constructor, static `create()` factory, static `reconstitute()` for persistence mapping, domain event recording via `recordEvent()`
- **Domain Events**: Extend `DomainEvent`, carry minimal data (IDs, not full aggregates)
- **Domain Services**: Stateless classes with domain logic that doesn't fit in a single aggregate, e.g. `CredentialsVerifier`
- **Use Cases**: Implement port interface, `@Service` + `@Transactional`, inject ports not repositories

### Error Handling

- Domain validation errors throw `ValidationException` or domain-specific exceptions extending `DomainException`
- `GlobalExceptionHandler` maps exceptions to HTTP responses
- Use `throw new ValidationException("message")` for invariant violations in value objects/aggregates
- Controllers should not catch exceptions; let them propagate to `GlobalExceptionHandler`

### Testing

- JUnit 5 + Mockito
- Unit tests for domain logic (no Spring context): `@ExtendWith(MockitoExtension.class)`
- Unit tests for aggregates/value objects: plain JUnit 5, no mocks
- Integration tests use H2 in-memory database (configured in `src/test/resources/application.properties`)
- Test class naming: `<ClassUnderTest>Test`
- Test method naming: `should<ExpectedBehavior>` e.g. `shouldActivateUser`, `shouldThrowExceptionWhenEmailAlreadyExists`

### JPA / Persistence

- Entity classes use `@Entity`, `@Table` with explicit column definitions
- UUIDs stored as `CHAR(36)`
- Timestamps stored as `Instant`
- Enums mapped with `@Enumerated(EnumType.STRING)`
- Flyway for migrations in `src/main/resources/db/migration/`
- `spring.jpa.hibernate.ddl-auto=validate` in production, `create-drop` in tests

### Spring Configuration

- REST API versioned: `/api/v1/`
- Stateless JWT authentication (no sessions)
- Security endpoints `/api/v1/auth/**` and `/api/v1/jobs/**` are public
- Java 17, Spring Boot 3.5.x
- Email: `SmtpEmailSenderAdapter` activo solo con `spring.mail.host`; sin esa propiedad se usa `NoOpEmailSenderAdapter` (logs WARN, no crashea)

---

## Domain Events — Design Rationale

This section explains how domain events work in this project and why certain
design choices were made.

### The Event Flow

```
Aggregado (creador) → Lista cerrada → Publisher (difusor)
```

The aggregate is the **sole creator** of events. Events accumulate in an internal
`List<DomainEvent>` via `AggregateRoot.recordEvent()`. The list is **closed**
— only the aggregate can add events. The `DomainEventPublisher` (infrastructure)
**only reads and publishes**, never creates or modifies.

### AggregateRoot

`AggregateRoot` holds a private `List<DomainEvent>` and exposes:

- `recordEvent(DomainEvent)` — protected, called only by aggregate methods
- `pullDomainEvents()` — public, returns an **unmodifiable** snapshot (`List.copyOf`) and clears the internal list

```java
// AggregateRoot.java
private final List<DomainEvent> domainEvents = new ArrayList<>();

protected void recordEvent(DomainEvent event) {
    this.domainEvents.add(event);  // solo el agregado agrega
}

public List<DomainEvent> pullDomainEvents() {
    List<DomainEvent> events = List.copyOf(domainEvents);  // snapshot inmutable
    domainEvents.clear();
    return events;
}
```

### DomainEvent

`DomainEvent` is the base abstract class for all domain events. Concrete events
extend it and add their payload fields.

### Why NOT `List<? super DomainEvent>`?

Using `List<? super DomainEvent>` would mean:

```
"Este método puede meter eventos en la lista"
```

That would allow the **publisher** to add events to the list, which breaks the
domain model:

- ❌ The publisher could invent events
- ❌ The aggregate is no longer the single source of truth
- ❌ Responsibilities get mixed (infrastructure modifying domain state)

### Why `List.copyOf()` (correct)?

Using `List.copyOf(domainEvents)` in `pullDomainEvents()` is correct because:

- ✅ Returns an **unmodifiable** snapshot — callers cannot add, remove, or clear events
- ✅ The internal `domainEvents` list is then safely cleared without affecting the returned snapshot
- ✅ The publisher gets events to publish but physically cannot mutate the list
- ✅ Encapsulation is preserved — events are generated inside the domain only

### Model in Plain Terms

| Component        | Role        | Can add events? | Description                        |
|-----------------|------------|:---:|-----------------------------------|
| `AggregateRoot` | 🎯 Creator |     ✅     | Calls `recordEvent()` in domain methods |
| `List<DomainEvent>` | 📦 Container |   ❌   | Closed — only `add()` from aggregate |
| `pullDomainEvents()` | 📸 Snapshot |   ❌   | Returns `List.copyOf(...)` — unmodifiable; clears internal list |
| `DomainEventPublisher` | 📡 Broadcaster |   ❌   | Reads the snapshot, publishes each event |

The publisher **does not create, does not modify, does not decide**. It only does:

```java
events.forEach(this::publish);  // read-only traversal
```

This connects with deeper DDD principles:

- **Encapsulamiento del dominio** — events stay within the aggregate boundary
- **Fuente de verdad** — the aggregate state is the only valid event source
- **Inconsistency prevention** — no duplicated or invented events
