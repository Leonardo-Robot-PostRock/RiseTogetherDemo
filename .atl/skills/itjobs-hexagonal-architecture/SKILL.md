# ITJobs Backend — Hexagonal Architecture

> Guía para implementar nuevas features siguiendo Arquitectura Hexagonal.

## Regla de Dependencias

```
Las dependencias siempre apuntan hacia el DOMINIO

Infrastructure → Application → Domain
```

> El dominio NO depende de nada externo.

## Checklist para Nueva Feature

1. **Dominio**: ¿Necesito un nuevo Agregado, Value Object o Evento?
2. **Puerto de Entrada**: Crear interfaz `XxxPort` en `application/ports/in/`
3. **Caso de Uso**: Crear `XxxUseCase` que implemente el puerto
4. **Command/Query**: Crear record inmutable con los datos de entrada
5. **Puertos de Salida**: ¿Necesito persistir o consultar? Crear interfaces
6. **Adaptadores de Salida**: Implementar puertos con JPA, etc.
7. **Adaptador de Entrada**: Crear endpoint REST en `infrastructure/adapters/in/rest/`
8. **Tests**: Unit tests para dominio, tests con mocks para casos de uso

## Estructura de Paquetes

```
bounded_context/
├── application/
│   ├── ports/
│   │   ├── in/           ← Puertos de ENTRADA
│   │   │   └── RegisterUserPort.java
│   │   └── out/          ← Puertos de SALIDA
│   │       └── SaveUserPort.java
│   └── usecases/
│       └── register/
│           ├── RegisterUserCommand.java
│           ├── RegisterUserResponse.java
│           └── RegisterUserUseCase.java
│
├── domain/
│   ├── aggregate/        ← Agregados
│   │   └── UserAggregate.java
│   ├── valueobjects/     ← Value Objects
│   │   └── Email.java
│   ├── event/           ← Domain Events
│   │   └── UserRegisteredEvent.java
│   └── exceptions/       ← Excepciones de dominio
│       └── UserAlreadyExistsException.java
│
└── infrastructure/
    ├── adapters/
    │   ├── in/rest/     ← Driving Adapters (REST)
    │   └── out/         ← Driven Adapters (JPA, etc.)
    │       └── persistence/
    └── config/          ← Configuración
```

## Value Object — Patrón

```java
public final class Email {
    private final String value;

    private Email(String value) {
        this.value = value;  // privado → fuerza factory method
    }

    public static Email of(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email cannot be empty");
        }
        String normalized = email.trim().toLowerCase();
        if (!PATTERN.matcher(normalized).matches()) {
            throw new ValidationException("Invalid email: " + email);
        }
        return new Email(normalized);
    }

    public String value() { return value; }

    // equals, hashCode...
}
```

## Aggregate — Patrón

```java
public class UserAggregate extends AggregateRoot {
    private final UserId id;
    private Username username;
    private Email email;
    private boolean active;

    // Constructor PRIVADO
    private UserAggregate(...) { ... }

    // Factory: nueva instancia
    public static UserAggregate create(Username username, Email email, ...) {
        return new UserAggregate(...);
    }

    // Factory: rehidratar desde DB
    public static UserAggregate reconstitute(...) {
        return new UserAggregate(...);
    }

    // Comportamiento
    public void activate() {
        if (this.active) throw new UserAlreadyActivatedException();
        this.active = true;
        recordEvent(new UserActivatedEvent(...));
    }
}
```

## Puerto de Salida — Patrón

```java
public interface SaveUserPort {
    UserAggregate save(UserAggregate user);
    boolean existsByEmail(Email email);
}
```

## Errores a Evitar

| ❌ Error | ✅ Corrección |
|----------|---------------|
| Inyectar JpaRepository en UseCase | Inyectar `XxxPort` (interfaz) |
| Validar en Controller | Validar en Value Object |
| Lógica de negocio en Controller | Mover a Aggregate |
| Usar `@Entity` en dominio | Separar Aggregate de Entity |
| Importar jakarta.persistence en dominio | El dominio no conoce JPA |

## Referencia

- `itjobs-docs/shared/hexagonal-onboarding.md`
- `itjobs-docs/shared/architecture.md`