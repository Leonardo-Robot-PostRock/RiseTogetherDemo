# Rise Together

> ⚠️ **Rama `demo`** — Esta rama es una versión de demostración pública del proyecto original.
> El código fuente completo, la documentación interna, los skills de AI y la configuración de agentes
> se encuentran en el repositorio privado.

> API REST para búsqueda de empleos en todos los sectores — Construcción / Testing / Deployment


## 🚀 Quick Start

```bash
# 1. Setup JDK (primera vez)
./setup-jdk.sh

# 2. Correr tests
./mvnw test

# 3. levántalo localmente
./mvnw spring-boot:run
```

## 🛠️ Stack Tecnológico

| Capa | Tecnología |
|------|-------------|
| **Lenguaje** | Java 25 |
| **Framework** | Spring Boot 4.0.6 |
| **Base de datos** | MySQL 9.5 + Flyway |
| ** Seguridad** | JWT + Spring Security |
| **Arquitectura** | Hexagonal (Ports & Adapters) |
| **Testing** | JUnit 5 + Mockito BDD |

## 📡 API Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Registro de usuario |
| POST | `/api/v1/auth/login` | Login con credentials |
| POST | `/api/v1/auth/login/google` | Login con Google OAuth |
| POST | `/api/v1/auth/verify-email` | Verificar email |
| POST | `/api/v1/account/change-password` | Cambiar password |
| POST | `/api/v1/jobs` | Crear oferta |
| GET | `/api/v1/jobs` | Buscar ofertas |

> Requiere JWT token para endpoints protegidos.

## 🏗️ Arquitectura

El proyecto sigue **Arquitectura Hexagonal**:

```
src/main/java/com/Rise Together/
├── authentication/     # Bounded Context: Auth
│   ├── application/   # Use Cases + Ports
│   ├── domain/        # Aggregates, Value Objects, Events
│   └── infrastructure/ # Adapters (REST, JPA, Security)
├── jobs/              # Bounded Context: Jobs
├── profiles/         # Bounded Context: Profiles
└── shared/           # Kernel compartido
```

### Patrones Clave

- **CQRS**: Separación Command/Query en autenticación
- **Value Objects**: Email, Username, Password (inmutables)
- **Domain Events**: Publicación de eventos post-commit

## 🧪 Testing

```bash
# Todos los tests
./mvnw test

# Un test específico
./mvnw test -Dtest=LoginUseCaseTest
```

## 🐳 Docker

```bash
# Levantar todo (MySQL + App)
docker compose up --build

# Solo la base de datos
docker compose up db
```

## 📚 Documentación (repositorio privado)

La documentación detallada, skills de AI, y configuración de agentes está en un repositorio privado separado. Solicita acceso si la necesitas.

## 📄License

MIT