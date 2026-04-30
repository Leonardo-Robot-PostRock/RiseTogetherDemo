# Arquitectura Hexagonal — Guía de Onboarding

> **Propósito**: Este documento explica la arquitectura hexagonal desde los conceptos abstractos hasta su implementación concreta en Rise Together, para facilitar la incorporación de nuevos desarrolladores al proyecto.

---

## 1. ¿Qué es la Arquitectura Hexagonal?

La **Arquitectura Hexagonal** (también llamada *Ports and Adapters*) fue propuesta por Alistair Cockburn en 2005. Su objetivo principal es **aislar la lógica de negocio** del mundo exterior (bases de datos, APIs, frameworks, etc.).

### La Metáfora del Hexágono

```
                    ┌─────────────────────────────────────────────┐
                    │              MUNDO EXTERIOR                 │
                    │   (HTTP, Bases de Datos, APIs externas)     │
                    └─────────────────────────────────────────────┘
                                         │
                    ┌────────────────────▼────────────────────────┐
                    │           ADAPTADORES (Adapters)            │
                    │  Traducen entre el mundo exterior y los     │
                    │  puertos de la aplicación                   │
                    └────────────────────┬────────────────────────┘
                                         │
                    ┌────────────────────▼────────────────────────┐
                    │             PUERTOS (Ports)                 │
                    │  Interfaces que definen cómo el núcleo      │
                    │  interactúa con el exterior                 │
                    └────────────────────┬────────────────────────┘
                                         │
                    ┌────────────────────▼────────────────────────┐
                    │         NÚCLEO DE LA APLICACIÓN             │
                    │   (Dominio + Casos de Uso)                  │
                    │   - Agregados                               │
                    │   - Value Objects                           │
                    │   - Reglas de negocio                       │
                    └─────────────────────────────────────────────┘
```

### Principio Fundamental

> **La lógica de negocio no debe depender de detalles de infraestructura.**

El dominio es el centro. Todo lo demás (HTTP, JPA, JWT, etc.) son detalles que se conectan mediante puertos y adaptadores.

---

## 2. Conceptos Clave

### 2.1 Puertos (Ports)

Los **puertos** son **interfaces** que definen los contratos de comunicación entre el núcleo y el exterior.

| Tipo de Puerto | Dirección | Propósito | Ejemplo Rise Together |
|----------------|-----------|-----------|----------------------|
| **Driving Port** (Puerto de entrada) | Exterior → Núcleo | Define qué puede hacer la aplicación | `RegisterUserPort` |
| **Driven Port** (Puerto de salida) | Núcleo → Exterior | Define qué necesita la aplicación del exterior | `SaveUserPort` |

### 2.2 Adaptadores (Adapters)

Los **adaptadores** son **implementaciones concretas** que conectan los puertos con tecnologías específicas.

| Tipo de Adaptador | Implementa | Tecnología | Ejemplo Rise Together |
|-------------------|------------|------------|----------------------|
| **Driving Adapter** | Puerto de entrada | REST, CLI, GraphQL | `AuthController` |
| **Driven Adapter** | Puerto de salida | JPA, Redis, API externa | `SaveUserPortAdapter` |

### 2.3 Núcleo de la Aplicación

El núcleo contiene:
- **Dominio**: Agregados, Value Objects, Eventos de Dominio, Excepciones
- **Casos de Uso**: Orquestan la lógica de negocio usando el dominio y los puertos

---

## 3. Estructura de Capas en Rise Together

```
authentication/
├── application/          ← CAPA DE APLICACIÓN (Casos de Uso + Puertos)
│   ├── ports/
│   │   ├── in/           ← Puertos de ENTRADA (qué puede hacer la app)
│   │   │   └── RegisterUserPort.java
│   │   └── out/          ← Puertos de SALIDA (qué necesita la app)
│   │       └── SaveUserPort.java
│   └── usecases/         ← Casos de uso (implementan puertos de entrada)
│       └── register/
│           ├── RegisterUserCommand.java
│           ├── RegisterUserResponse.java
│           └── RegisterUserUseCase.java
│
├── domain/               ← CAPA DE DOMINIO (reglas de negocio puras)
│   ├── aggregate/
│   │   └── UserAggregate.java
│   ├── valueobjects/
│   │   ├── Username.java
│   │   └── HashedPassword.java
│   ├── event/
│   │   └── UserRegisteredEvent.java
│   └── exceptions/
│       └── UserAlreadyExistsException.java
│
└── infrastructure/       ← CAPA DE INFRAESTRUCTURA (detalles técnicos)
    ├── adapters/
    │   ├── in/rest/      ← Adaptadores de ENTRADA (HTTP)
    │   │   └── AuthController.java
    │   └── out/          ← Adaptadores de SALIDA
    │       ├── persistence/  ← JPA/MySQL
    │       │   ├── SaveUserPortAdapter.java
    │       │   └── jpa/
    │       │       ├── JpaUserRepositoryAdapter.java
    │       │       └── entities/UserEntity.java
    │       └── security/     ← JWT, BCrypt
    │           └── BCryptPasswordEncoderAdapter.java
    ├── config/           ← Configuración de Spring
    └── events/           ← Listeners de eventos
```

---

## 4. Flujo de una Petición: Registro de Usuario

Veamos cómo fluye una petición de registro a través de las capas:

```
┌─────────────┐    ┌─────────────────┐    ┌──────────────────┐    ┌────────────────┐
│   Cliente   │───▶│  AuthController │───▶│ RegisterUserPort │───▶│ RegisterUser   │
│   (HTTP)    │    │  (Driving       │    │ (Driving Port)   │    │ UseCase        │
│             │    │   Adapter)      │    │                  │    │                │
└─────────────┘    └─────────────────┘    └──────────────────┘    └───────┬────────┘
                                                                          │
                                                                          ▼
┌─────────────┐    ┌─────────────────┐    ┌──────────────────┐    ┌────────────────┐
│   MySQL     │◀───│ JpaUserRepo     │◀───│ SaveUserPort     │◀───│ UserAggregate  │
│   (DB)      │    │ Adapter         │    │ (Driven Port)    │    │ (Domain)       │
│             │    │ (Driven Adapter)│    │                  │    │                │
└─────────────┘    └─────────────────┘    └──────────────────┘    └────────────────┘
```

### Paso a paso:

1. **Cliente** envía `POST /api/v1/auth/register` con JSON
2. **AuthController** (Driving Adapter) recibe la petición y convierte el DTO a Command
3. **RegisterUserPort** (interface) define el contrato `execute(RegisterUserCommand)`
4. **RegisterUserUseCase** implementa el puerto y orquesta la lógica
5. **UserAggregate** aplica las reglas de negocio (validaciones, creación)
6. **SaveUserPort** (interface) define el contrato `save(UserAggregate)`
7. **SaveUserPortAdapter** (Driven Adapter) implementa el puerto usando JPA
8. **MySQL** persiste los datos

---

## 5. Ejemplos Concretos de Rise Together

### 5.1 Puerto de Entrada (Driving Port)

```java
// authentication/application/ports/in/RegisterUserPort.java
public interface RegisterUserPort {
    RegisterUserResponse execute(RegisterUserCommand command);
}
```

**¿Por qué?** Define QUÉ puede hacer la aplicación (registrar usuarios), sin decir CÓMO ni QUIÉN lo invoca.

### 5.2 Puerto de Salida (Driven Port)

```java
// authentication/application/ports/out/SaveUserPort.java
public interface SaveUserPort {
    UserAggregate save(UserAggregate user);
    boolean existsByEmail(Email email);
}
```

**¿Por qué?** Define QUÉ necesita la aplicación del exterior (persistir usuarios), sin acoplarse a JPA, MongoDB, etc.

### 5.3 Caso de Uso (Implementa Puerto de Entrada)

```java
// authentication/application/usecases/register/RegisterUserUseCase.java
@Service
@Transactional
public class RegisterUserUseCase implements RegisterUserPort {
    
    private final SaveUserPort saveUserPort;           // Puerto de salida
    private final PasswordEncoderPort passwordEncoder; // Puerto de salida
    private final DomainEventPublisher domainEventPublisher;
    
    @Override
    public RegisterUserResponse execute(RegisterUserCommand command) {
        // 1. Crear Value Objects (validación en fábrica)
        Email email = Email.of(command.email());
        Username username = Username.of(command.username());
        Password rawPassword = Password.of(command.password());
        
        // 2. Verificar regla de negocio
        if (saveUserPort.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email.value());
        }
        
        // 3. Usar dominio
        String hashedValue = passwordEncoder.encode(rawPassword.value());
        HashedPassword hashedPassword = HashedPassword.fromHash(hashedValue);
        UserAggregate user = UserAggregate.create(username, email, hashedPassword);
        
        // 4. Persistir via puerto
        UserAggregate savedUser = saveUserPort.save(user);
        domainEventPublisher.publishAll(savedUser.pullDomainEvents());
        
        // 5. Retornar respuesta
        return new RegisterUserResponse(...);
    }
}
```

**Observaciones clave:**
- El caso de uso **inyecta puertos**, no implementaciones concretas
- Usa **Value Objects** del dominio (`Email`, `Username`)
- Delega la persistencia al **puerto de salida** `SaveUserPort`
- No conoce JPA, MySQL, ni ningún detalle de infraestructura

### 5.4 Adaptador de Entrada (Driving Adapter)

```java
// authentication/infrastructure/adapters/in/rest/AuthController.java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    private final RegisterUserPort registerUserPort; // Inyecta el PUERTO, no el UseCase
    
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        // 1. Convertir DTO → Command
        RegisterUserCommand command = new RegisterUserCommand(
            request.username(), 
            request.email(), 
            request.password()
        );
        
        // 2. Invocar puerto
        RegisterUserResponse response = registerUserPort.execute(command);
        
        // 3. Convertir Response → DTO
        RegisterResponse dto = new RegisterResponse(...);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
```

**Observaciones clave:**
- El controlador inyecta `RegisterUserPort` (interfaz), no `RegisterUserUseCase` (implementación)
- Traduce DTOs de HTTP a Commands del dominio
- No contiene lógica de negocio

### 5.5 Adaptador de Salida (Driven Adapter)

```java
// authentication/infrastructure/adapters/out/persistence/SaveUserPortAdapter.java
@Component
public class SaveUserPortAdapter implements SaveUserPort {
    
    private final UserWriterRepository userRepository;
    
    @Override
    public UserAggregate save(UserAggregate user) {
        return userRepository.save(user);
    }
    
    @Override
    public boolean existsByEmail(Email email) {
        return userRepository.existsByEmail(email);
    }
}
```

**Observaciones clave:**
- Implementa el **puerto de salida** `SaveUserPort`
- Delega a un repositorio JPA específico
- Aísla los detalles de JPA del caso de uso

### 5.6 Value Object (Dominio)

```java
// shared/domain/valueobjects/Email.java
public final class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@...");
    private final String value;
    
    private Email(String value) {
        this.value = value;
    }
    
    public static Email of(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email cannot be empty");
        }
        String normalized = email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new ValidationException("Invalid email format: " + email);
        }
        return new Email(normalized);
    }
    
    public String value() { return value; }
    
    // equals, hashCode, toString...
}
```

**Características DDD:**
- Constructor **privado** → fuerza uso de factory method
- Factory method `of()` → valida invariantes
- **Inmutable** → `final class`, campo `final`
- **Autoverificable** → no puede existir un Email inválido

### 5.7 Agregado (Dominio)

```java
// authentication/domain/aggregate/UserAggregate.java
public class UserAggregate extends AggregateRoot {
    private final UserId id;
    private Username username;
    private Email email;
    private HashedPassword password;
    private boolean active;
    // ...
    
    // Constructor PRIVADO
    private UserAggregate(UserId id, Username username, Email email, 
                          HashedPassword password, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        // ...
    }
    
    // Factory method para CREAR nuevo usuario
    public static UserAggregate create(Username username, Email email, 
                                        HashedPassword hashedPassword) {
        return new UserAggregate(UserId.generate(), username, email, 
                                  hashedPassword, Timestamp.now());
    }
    
    // Factory method para RECONSTRUIR desde persistencia
    public static UserAggregate reconstitute(UserId id, Username username, 
                                              Email email, /* ... */) {
        UserAggregate user = new UserAggregate(id, username, email, password, createdAt);
        user.active = active;
        // ... sin efectos secundarios
        return user;
    }
    
    // Comportamiento de dominio
    public void activate() {
        if (this.active) {
            throw new UserAlreadyActivatedException("User is already active");
        }
        this.active = true;
        this.updatedAt = Timestamp.now();
        recordEvent(new UserActivatedEvent(this.id.value().toString(), this.email.value()));
    }
}
```

**Características DDD:**
- Constructor **privado** → control total sobre creación
- `create()` → para nuevas instancias (genera ID, registra eventos)
- `reconstitute()` → para rehidratar desde DB (sin eventos, sin efectos secundarios)
- **Métodos de comportamiento** → `activate()`, `deactivate()`, `verifyEmail()`
- **Eventos de dominio** → `recordEvent()` registra eventos para publicar después

---

## 6. Regla de Dependencias

```
┌─────────────────────────────────────────────────────────────────┐
│                      INFRASTRUCTURE                             │
│  (AuthController, JpaUserRepositoryAdapter, BCryptAdapter)      │
│                           │                                     │
│                           │ depende de                          │
│                           ▼                                     │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                    APPLICATION                           │   │
│  │  (RegisterUserUseCase, RegisterUserPort, SaveUserPort)   │   │
│  │                           │                              │   │
│  │                           │ depende de                   │   │
│  │                           ▼                              │   │
│  │  ┌─────────────────────────────────────────────────┐     │   │
│  │  │                    DOMAIN                       │     │   │
│  │  │  (UserAggregate, Email, UserId, DomainEvents)   │     │   │
│  │  │                                                 │     │   │
│  │  │  ★ NO DEPENDE DE NADA EXTERNO ★                 │     │   │
│  │  └─────────────────────────────────────────────────┘     │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### Regla de oro:
> **Las dependencias siempre apuntan hacia adentro (hacia el dominio).**

- ✅ `AuthController` → `RegisterUserPort` → `UserAggregate`
- ❌ `UserAggregate` → `JpaRepository` (¡nunca!)

---

## 7. Beneficios de esta Arquitectura

| Beneficio | Descripción | Ejemplo en Rise Together |
|-----------|-------------|-------------------------|
| **Testeable** | El dominio se puede testear sin Spring, sin DB | `UserAggregateTest` no usa mocks de JPA |
| **Desacoplado** | Cambiar MySQL por MongoDB solo afecta adaptadores | `JpaUserRepositoryAdapter` → `MongoUserRepositoryAdapter` |
| **Mantenible** | La lógica de negocio está centralizada | Reglas de usuario están en `UserAggregate`, no en controladores |
| **Evolutivo** | Agregar un CLI o GraphQL no afecta casos de uso | Nuevo `GraphQLAuthAdapter` usaría el mismo `RegisterUserPort` |

---

## 8. Checklist para Nuevos Features

Cuando agregues una nueva funcionalidad:

- [ ] **1. Dominio**: ¿Necesito un nuevo Agregado, Value Object o Evento?
- [ ] **2. Puerto de Entrada**: Crear interfaz `XxxPort` en `application/ports/in/`
- [ ] **3. Caso de Uso**: Crear `XxxUseCase` que implemente el puerto
- [ ] **4. Command/Query**: Crear record inmutable con los datos de entrada
- [ ] **5. Puertos de Salida**: ¿Necesito persistir o consultar algo? Crear interfaces
- [ ] **6. Adaptadores de Salida**: Implementar puertos con JPA, APIs externas, etc.
- [ ] **7. Adaptador de Entrada**: Crear endpoint REST en `infrastructure/adapters/in/rest/`
- [ ] **8. Tests**: Unit tests para dominio, tests con mocks para casos de uso

---

## 9. Errores Comunes a Evitar

| ❌ Error | ✅ Corrección |
|---------|--------------|
| Inyectar `JpaRepository` en UseCase | Inyectar `SaveUserPort` (interfaz) |
| Validar email en el Controller | Validar en `Email.of()` (Value Object) |
| Lógica de negocio en Controller | Mover a Agregado o UseCase |
| Usar `@Entity` en clase de dominio | Separar `UserAggregate` (dominio) de `UserEntity` (JPA) |
| Importar `jakarta.persistence` en dominio | El dominio no debe conocer JPA |
| Publicar eventos en el Controller | Publicar en el UseCase después de `save()` |

---

## 10. Recursos Adicionales

- [Artículo original de Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [AGENTS.md](../AGENTS.md) — Comandos de build/test y convenciones del proyecto
- [architecture.md](architecture.md) — Configuración de entornos y profiles
- [testing.md](testing.md) — Estrategia de testing

---

## Resumen Visual

```
┌──────────────────────────────────────────────────────────────────────────┐
│                         Rise Together                                    │
│                                                                          │
│   ┌──────────────────────────────────────────────────────────────────┐   │ 
│   │  INFRASTRUCTURE (adapters/in, adapters/out, config)              │   │
│   │                                                                  │   │
│   │   AuthController ──▶ RegisterUserPort ◀── RegisterUserUseCase    │   │
│   │                                                 │                │   │
│   │                                                 ▼                │   │
│   │   JpaUserRepo ◀── UserWriterRepository ◀── SaveUserPort          │   │
│   │        │                                                         │   │
│   │        ▼                                                         │   │
│   │   UserEntity ←mapper→ UserAggregate (Domain)                     │   │
│   │                            │                                     │   │
│   │                    ┌───────┴───────┐                             │   │
│   │                    │    Email      │  Value Objects              │   │
│   │                    │   Username    │                             │   │
│   │                    │ HashedPassword│                             │   │
│   │                    └───────────────┘                             │   │
│   └──────────────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────────────┘
```

---

*Última actualización: Abril 2026*

