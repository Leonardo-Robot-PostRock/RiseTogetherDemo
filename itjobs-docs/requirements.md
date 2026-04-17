# Requisitos — ITJobs Backend

> ⚠️ **Versión extendida disponible**: Ver [`requirements-v2.md`](requirements-v2.md) para la documentación completa con matching explicable, moderación, monetización B2B, onboarding, analítica y nuevos bounded contexts.

## Bounded Contexts

| Bounded Context    | Estado                 | Paquete                            |
|--------------------|------------------------|------------------------------------|
| **Authentication** | Implementado (parcial) | `com.ITJobsBackend.authentication` |
| **Jobs**           | Implementado (parcial) | `com.ITJobsBackend.jobs`           |
| **Shared**         | Implementado           | `com.ITJobsBackend.shared`         |
| **Applications**   | Pendiente (DB creada)  | `com.ITJobsBackend.applications`   |
| **Profiles**       | Pendiente (DB creada)  | `com.ITJobsBackend.profiles`       |
| **Administration** | Pendiente              | `com.ITJobsBackend.administration` |

### Estado por contexto

**Authentication** — Implementado parcialmente

- [x] Registro de usuario
- [x] Login con JWT
- [x] Filtro JWT de autenticación
- [x] Verificar email
- [x] Cambiar password
- [ ] Actualizar email

**Jobs** — Implementado parcialmente

- [x] Crear oferta de empleo
- [x] Buscar ofertas por título
- [x] Cerrar/desactivar oferta
- [ ] Asignar skills a oferta (DB creada, sin controller)
- [ ] Asignar categorías (DB creada, sin controller)

**Applications** — DB creada, sin código

- [ ] Postularse a oferta
- [ ] Ver postulaciones
- [ ] Cambiar estado de postulación
- [ ] Guardar/eliminar favoritos

**Profiles** — DB creada, sin código

- [ ] Crear/gestionar perfil candidato
- [ ] Crear/gestionar perfil empleador
- [ ] Registrar reclutador

**Administration** — Pendiente

- [ ] Gestionar categorías
- [ ] Gestionar usuarios
- [ ] Gestionar roles

---

## Actores del sistema

| Actor          | Descripción                                                                        |
|----------------|------------------------------------------------------------------------------------|
| **Candidato**  | Usuario que busca empleo. Se postula a ofertas, guarda favoritos, gestiona perfil. |
| **Empleador**  | Empresa que publica ofertas de trabajo. Gestiona reclutadores.                     |
| **Reclutador** | Persona que trabaja para un empleador. Gestiona postulaciones y ofertas.           |
| **Moderador**  | Usuario que modera contenido (reportes, spam, ofertas inapropiadas).               |
| **Admin**      | Administrador del sistema. Gestiona usuarios, roles, categorías.                   |

---

## Requisitos Funcionales

### Bounded Context: Authentication

| ID    | Requisito                                                                      | Estado |
|-------|--------------------------------------------------------------------------------|--------|
| RF-01 | Registrar usuario con username, email y password válido                        | ✅      |
| RF-02 | Login con email/password → genera access token (15min) + refresh token (7 días)| ✅      |
| RF-03 | Verificar email del usuario                                                    | ✅      |
| RF-04 | Activar/desactivar usuario                                                     | ❌      |
| RF-05 | Cambiar password de usuario                                                    | ✅      |
| RF-06 | Actualizar email (requiere re-verificación)                                    | ❌      |

### Bounded Context: Profiles

| ID    | Requisito                                                                |
|-------|--------------------------------------------------------------------------|
| RF-07 | Crear perfil de candidato (nombre, apellido, resumen, ubicación, skills) |
| RF-08 | Actualizar perfil de candidato                                           |
| RF-09 | Crear perfil de empleador (empresa, industria, website, contacto)        |
| RF-10 | Actualizar perfil de empleador                                           |
| RF-11 | Registrar reclutador vinculado a un empleador                            |

### Bounded Context: Jobs

| ID    | Requisito                                                                       |
|-------|---------------------------------------------------------------------------------|
| RF-12 | Crear oferta de empleo (título, empresa, descripción, salario, tipo, ubicación) |
| RF-13 | Buscar ofertas por título                                                       |
| RF-14 | Cerrar oferta de empleo                                                         |
| RF-15 | Desactivar oferta de empleo                                                     |
| RF-16 | Agregar skills requeridos a una oferta                                          |
| RF-17 | Asignar categorías a una oferta                                                 |

### Bounded Context: Applications

| ID    | Requisito                                                              |
|-------|------------------------------------------------------------------------|
| RF-18 | Candidato se postula a una oferta con CV y carta de presentación       |
| RF-19 | Ver postulaciones por candidato                                        |
| RF-20 | Cambiar estado de postulación (PENDING → REVIEWED → ACCEPTED/REJECTED) |
| RF-21 | Guardar oferta como favorita                                           |
| RF-22 | Eliminar oferta de favoritos                                           |

### Bounded Context: Categories

| ID    | Requisito                                   |
|-------|---------------------------------------------|
| RF-23 | Crear/editar/eliminar categorías de trabajo |

### Bounded Context: Moderation

| ID    | Requisito                                                         |
|-------|-------------------------------------------------------------------|
| RF-24 | Reportar contenido inapropiado (ofertas, postulaciones, usuarios) |
| RF-25 | Revisar y aprobar/rechazar reportes                               |
| RF-26 | Banear usuarios por conducta inapropiada                          |
| RF-27 | Restaurar contenido eliminado (apelaciones)                       |

---

## Requisitos No Funcionales

| ID     | Categoría         | Requisito                                                                    |
|--------|-------------------|------------------------------------------------------------------------------|
| RNF-01 | Autenticación     | Autenticación stateless con JWT (sin sesiones de servidor)                   |
| RNF-02 | Seguridad         | Password hasheado con BCrypt                                                 |
| RNF-03 | Arquitectura      | Hexagonal (Ports & Adapters) con DDD táctico                                 |
| RNF-04 | Base de datos     | MySQL 8.0 en producción, H2 en memoria para tests                            |
| RNF-05 | Migraciones       | Esquema versionado con Flyway (V1–V10)                                       |
| RNF-06 | Despliegue        | Docker Compose (MySQL + Spring Boot)                                         |
| RNF-07 | Manejo de errores | GlobalExceptionHandler centralizado                                          |
| RNF-08 | Validación        | Jakarta Validation en capa REST                                              |
| RNF-09 | Eventos           | Domain events para desacoplar bounded contexts                               |
| RNF-10 | Moneda            | Salarios en BigDecimal (nunca double)                                        |
| RNF-11 | Perfiles          | Spring profiles: `dev` (local), `prod` (Docker), `staging` (pendiente)       |
| RNF-12 | Charset           | Base de datos con charset `utf8mb4_unicode_ci`                               |
| RNF-13 | UUID              | UUIDs mapeados como `VARCHAR(36)` vía `@JdbcTypeCode(SqlTypes.VARCHAR)`      |
