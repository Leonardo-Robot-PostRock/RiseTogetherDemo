# ITJobs Backend — CQRS Pattern

> Implementación de CQRS en el bounded context `authentication`.

## Concepto

**CQRS** = Command Query Responsibility Segregation

Separa las rutas de lectura y escritura en dos puertos de salida distintos:

```
                    ┌─────────────────────┐
                    │    Use Case          │
                    └──────┬──────┬────────┘
                           │      │
                 mutates?  │ yes  │ no
                           ▼      ▼
                   ┌───────────┐ ┌─────────────┐
                   │LoadUserPort│ │QueryUserPort│
                   │ (command)  │ │  (query)    │
                   └───────┬─────┘ └──────┬──────┘
                           │             │
                           ▼             ▼
                    UserAggregate   UserView
                    (write model)  (read model)
```

## Los Dos Puertos

### LoadUserPort (command side)

```java
public interface LoadUserPort {
    Optional<UserAggregate> findByEmail(Email email);
    Optional<UserAggregate> findById(UserId id);
}
```
- Devuelve **UserAggregate** (modelo de escritura)
- Solo para use cases que **mutan** el agregado
- Implementado por `LoadUserPortAdapter`

### QueryUserPort (query side)

```java
public interface QueryUserPort {
    Optional<UserView> findByEmail(Email email);
    Optional<UserView> findById(UserId id);
    boolean existsByEmail(Email email);
    List<UserId> findUnverifiedUserIdsBefore(Instant cutoff);
}
```
- Devuelve **UserView** (record, proyección plana)
- Solo para use cases de **solo lectura**
- Implementado por `QueryUserPortAdapter`

## UserView (read model)

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
- Es un `record` — inmutable, sin lógica
- Se construye directamente desde `UserEntity`
- No pasa por `UserAggregate` en consultas

## Mapping Use Cases → Puertos

| Use Case | Side | Puerto de usuario |
|----------|------|-------------------|
| `LoginUseCase` | Read | `QueryUserPort` |
| `RefreshTokenUseCase` | Read | `QueryUserPort` |
| `ForgotPasswordUseCase` | Read | `QueryUserPort` |
| `RegisterUserUseCase` | Write | `QueryUserPort` + `SaveUserPort` |
| `VerifyEmailUseCase` | Write | `LoadUserPort` + `SaveUserPort` |
| `ChangePasswordUseCase` | Write | `LoadUserPort` + `SaveUserPort` |
| `GoogleAuthUseCase` | Write | `LoadUserPort` + `SaveUserPort` |

## Por Qué CQRS

- ✅ El read model es una proyección plana y barato
- ✅ La query side puede optimizarse (EXISTS, proyecciones parciales)
- ✅ El write model conserva toda su riqueza sin contaminar la lectura
- ✅ Evita instanciar `UserAggregate` completo solo para leer credenciales

## Errores a Evitar

- ❌ No usar `LoadUserPort` en LoginUseCase (solo lee)
- ❌ No usar `QueryUserPort` en VerifyEmailUseCase (mutates)
- ❌ No devolver agregados desde puertos de query

## Referencia

- `itjobs-docs/shared/architecture.md` — sección CQRS
- `AGENTS.md` — use cases table