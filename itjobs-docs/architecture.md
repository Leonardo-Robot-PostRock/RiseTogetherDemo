# Arquitectura — ITJobs Backend

## Profiles de Spring Boot

| Profile | Propósito | DB | Credenciales | Logging |
|---------|-----------|-----|-------------|---------|
| `dev` | Desarrollo local | localhost:3306 (Docker) | Hardcodeadas en `application-dev.properties` | DEBUG |
| `prod` | Producción | Env vars (Docker Compose) | Env vars | WARN |

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

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `MYSQL_ROOT_PASSWORD` | Password root de MySQL | `rootpass` |
| `MYSQL_DATABASE` | Nombre de la base de datos | `itjobs` |
| `DB_URL` | URL JDBC completa | `jdbc:mysql://db:3306/itjobs?...` |
| `DB_USERNAME` | Usuario de la DB | `root` |
| `DB_PASSWORD` | Password de la DB | (igual a `MYSQL_ROOT_PASSWORD`) |
| `JWT_SECRET` | Secreto para firmar JWT (min 256 bits) | Ver `.env.example` |

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

| Servicio | Imagen | Puerto | Healthcheck |
|----------|--------|--------|-------------|
| `db` | mysql:8.0 | 3306 | `mysqladmin ping` |
| `app` | Build local (Dockerfile) | 9090 | depende de `db` healthy |

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