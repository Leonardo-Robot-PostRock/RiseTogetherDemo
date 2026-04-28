# Requisitos v2 — ITJobs Backend

> **Fecha**: 2026-04-16  
> **Versión**: 2.0  
> **Alcance**: Extensión del producto para competir con portales de empleo modernos.  
> **Principio rector**: Conservar todo lo existente, extender donde sea necesario, reemplazar solo cuando el modelo actual impida el caso de uso.

---

## Resumen Ejecutivo

ITJobs Backend tiene una base sólida: arquitectura hexagonal, DDD táctico, JWT stateless, Flyway, bounded contexts claros y tablas para los actores principales. Sin embargo, el producto actual es un CRUD de ofertas con autenticación. Para competir, necesita:

1. **Matching explicable** entre candidatos y ofertas (no solo keywords).
2. **Transparencia**: salarios obligatorios, explicación del match, moderación de contenido.
3. **Onboarding guiado** para reclutadores y candidatos.
4. **Monetización B2B**: planes, ofertas destacadas, analítica para empresas.
5. **Moderación robusta**: reportes, decisiones, apelaciones, auditoría.
6. **Separación UI pública / dashboard privado** a nivel de API y permisos.

Todo se diseña para implementar incrementalmente sin romper lo existente.

---

## 1. Requisitos Funcionales — Nuevos y Refinados

### Leyenda de prioridad

| Símbolo | Significado |
|---------|-------------|
| 🔴 | Obligatorio |
| 🟡 | Recomendable |
| 🟢 | Conveniente |

### Refinamientos a requisitos existentes

| ID Original | Refinamiento | Motivo |
|-------------|-------------|--------|
| RF-12 | `jobs` debe tener `employer_id` FK a `employers` además de `company` (campo legacy) | Actualmente `company` es un string libre; no permite vincular ofertas al empleador real, ni dashboard, ni analítica. |
| RF-16 | `job_skills` debe soportar `requirement_level` (REQUIRED / DESIRED / OPTIONAL) y referencia a catálogo de skills | Skills como strings simples impiden matching semántico, sinónimos y niveles. |
| RF-18 | `applications` debe soportar `match_score`, `match_explanation`, `recruiter_notes`, pipeline extendido | El pipeline actual (PENDING→REVIEWED→ACCEPTED/REJECTED) es insuficiente para reclutamiento real. |
| RF-07 | `candidates` debe soportar `years_of_experience`, `desired_salary_min/max`, `desired_employment_type`, `open_to_remote` | Perfil demasiado básico para matching. |

### Nuevos requisitos funcionales

#### Bounded Context: Jobs (refinado)

| ID | Requisito | Prioridad | BC | Observaciones |
|----|-----------|-----------|-----|---------------|
| RF-28 | Vincular oferta a empleador mediante `employer_id` FK | 🔴 | Jobs | Reemplaza dependencia en `jobs.company` libre. Campo `company` se mantiene por compatibilidad temporal. |
| RF-29 | Buscar ofertas por múltiples filtros: título, ubicación, tipo empleo, rango salarial, skills, categoría, remoto | 🔴 | Jobs | Extiende RF-13 (solo título). |
| RF-30 | Crear oferta como borrador (DRAFT) y publicar después | 🟡 | Jobs | El status DRAFT ya existe en el enum pero no hay flujo. |
| RF-31 | Expiración automática de ofertas tras N días configurables | 🟡 | Jobs | Agrega `expires_at` a `jobs`. |
| RF-32 | Linter de ofertas: advertir requisitos excesivos, incoherentes o poco inclusivos al publicar | 🟡 | Jobs | Servicio de dominio que valida la oferta antes de cambiar a OPEN. |
| RF-33 | Marcar oferta como destacada/promocionada (requiere plan activo) | 🔴 | Jobs + Billing | Agrega `featured` y `featured_until` a `jobs`. |

#### Bounded Context: Matching (nuevo)

| ID | Requisito | Prioridad | BC | Observaciones |
|----|-----------|-----------|-----|---------------|
| RF-34 | Catálogo maestro de skills con sinónimos, skills equivalentes y adyacentes | 🔴 | Matching | Tabla `skills` normalizada + `skill_synonyms` + `skill_relationships`. |
| RF-35 | Clasificar skills de oferta como REQUIRED, DESIRED, OPTIONAL | 🔴 | Matching | Columna `requirement_level` en `job_skills`. |
| RF-36 | Registrar nivel de competencia del candidato por skill (BEGINNER, INTERMEDIATE, ADVANCED, EXPERT) | 🔴 | Matching | Columna `proficiency_level` en `candidate_skills`. |
| RF-37 | Registrar años de experiencia por skill del candidato | 🟡 | Matching | Columna `years_experience` en `candidate_skills`. |
| RF-38 | Calcular match score entre candidato y oferta considerando: skills exactos, transferibles, parciales, emergentes | 🔴 | Matching | Servicio de dominio. Score 0–100. |
| RF-39 | Generar explicación textual del match visible para candidato y reclutador | 🔴 | Matching | JSON estructurado almacenado en `applications.match_explanation`. |
| RF-40 | Clasificar candidatos por tipo de match: Exact, Transferable Skills, Partial, Emergent | 🔴 | Matching | Derivado del score y las reglas. |
| RF-41 | Mostrar candidatos valiosos aunque no cumplan el 100% de keywords | 🔴 | Matching | El algoritmo no descarta por falta de keyword exacta. |
| RF-42 | Sugerir ofertas al candidato basado en su perfil (reverse matching) | 🟡 | Matching | Reutiliza el motor de matching. |

#### Bounded Context: Applications (refinado)

| ID | Requisito | Prioridad | BC | Observaciones |
|----|-----------|-----------|-----|---------------|
| RF-43 | Pipeline de postulación extendido: PENDING → SCREENING → INTERVIEW → OFFER → ACCEPTED / REJECTED / WITHDRAWN | 🔴 | Applications | Reemplaza el pipeline básico de RF-20. |
| RF-44 | Reclutador puede agregar notas privadas a una postulación | 🔴 | Applications | Tabla `application_notes`. |
| RF-45 | Reclutador puede mover candidatos entre etapas del pipeline | 🔴 | Applications | Evento `ApplicationStageChangedEvent`. |
| RF-46 | Candidato puede retirar su postulación (WITHDRAWN) | 🟡 | Applications | Transición válida desde cualquier estado excepto ACCEPTED/REJECTED. |
| RF-47 | Historial de cambios de estado de la postulación | 🟡 | Applications | Tabla `application_status_history`. |

#### Bounded Context: Profiles (refinado)

| ID | Requisito | Prioridad | BC | Observaciones |
|----|-----------|-----------|-----|---------------|
| RF-48 | Candidato registra años de experiencia total, salario deseado, preferencia de remoto | 🔴 | Profiles | Columnas en `candidates`. |
| RF-49 | Candidato puede agregar proyectos/portafolio como evidencia de skills | 🟡 | Profiles | Tabla `candidate_projects`. |
| RF-50 | Empleador puede configurar su logo, descripción y cultura de empresa | 🟡 | Profiles | Columnas en `employers`. |
| RF-51 | Reclutador ve dashboard con sus ofertas, postulaciones y pipeline | 🔴 | Profiles | Endpoint de API, no tabla nueva. |

#### Bounded Context: Moderation (extendido)

| ID | Requisito | Prioridad | BC | Observaciones |
|----|-----------|-----------|-----|---------------|
| RF-52 | Reportar oferta con motivo estructurado (SPAM, MISLEADING, DISCRIMINATORY, IMMORAL, DUPLICATE, OTHER) | 🔴 | Moderation | Tabla `reports`. |
| RF-53 | Moderador revisa reporte y toma decisión (APPROVED, REJECTED, ESCALATED) con motivo | 🔴 | Moderation | Tabla `moderation_decisions`. |
| RF-54 | Usuario apela decisión de moderación con justificación | 🔴 | Moderation | Tabla `moderation_appeals`. |
| RF-55 | Restaurar contenido tras apelación aceptada | 🔴 | Moderation | Actualiza status del contenido original. |
| RF-56 | Feedback estructurado sobre ofertas (rating 1-5 en dimensiones predefinidas, sin texto libre) | 🟢 | Moderation | Tabla `job_feedback`. Solo candidatos que se postularon. |
| RF-57 | Banear usuario temporal o permanentemente con motivo | 🟡 | Moderation | Columnas `banned_until`, `ban_reason` en `users`. |

#### Bounded Context: Onboarding (nuevo)

| ID | Requisito | Prioridad | BC | Observaciones |
|----|-----------|-----------|-----|---------------|
| RF-58 | Guided tour de onboarding para candidatos (pasos configurables) | 🟡 | Onboarding | Tablas `onboarding_flows`, `onboarding_steps`, `user_onboarding_progress`. |
| RF-59 | Guided tour de onboarding para reclutadores/empleadores | 🟡 | Onboarding | Mismo sistema, distinto flujo. |
| RF-60 | Coach marks / tooltips contextuales por feature | 🟢 | Onboarding | Tabla `coach_marks` + progreso. |
| RF-61 | Repetir tutorial desde menú de ayuda | 🟢 | Onboarding | Reset del progreso del usuario. |

#### Bounded Context: Help Center (nuevo)

| ID | Requisito | Prioridad | BC | Observaciones |
|----|-----------|-----------|-----|---------------|
| RF-62 | Centro de ayuda con artículos categorizados | 🟡 | Help Center | Tablas `help_articles`, `help_categories`. |
| RF-63 | Artículos pueden incluir video embebido (URL) | 🟡 | Help Center | Columna `video_url` en `help_articles`. |
| RF-64 | Buscar artículos por título y contenido | 🟢 | Help Center | Fulltext index. |

#### Bounded Context: Billing (nuevo)

| ID | Requisito | Prioridad | BC | Observaciones |
|----|-----------|-----------|-----|---------------|
| RF-65 | Definir planes de suscripción (FREE, STARTER, PROFESSIONAL, ENTERPRISE) con límites | 🔴 | Billing | Tabla `subscription_plans`. |
| RF-66 | Empleador suscribe a un plan | 🔴 | Billing | Tabla `subscriptions`. |
| RF-67 | Validar límites del plan al publicar oferta o acceder a feature premium | 🔴 | Billing | Servicio de dominio. |
| RF-68 | Registrar pagos asociados a suscripciones | 🟡 | Billing | Tabla `payments`. Integración con pasarela es futuro. |
| RF-69 | Promocionar oferta como destacada (consume crédito o requiere plan) | 🔴 | Billing | Vinculado a RF-33. |
| RF-70 | Límites por plan: ofertas activas, postulaciones visibles, exportación, acceso a matching avanzado | 🔴 | Billing | Columnas en `subscription_plans`. |

#### Bounded Context: Analytics (nuevo)

| ID | Requisito | Prioridad | BC | Observaciones |
|----|-----------|-----------|-----|---------------|
| RF-71 | Dashboard de empleador: ofertas publicadas, postulaciones recibidas, tasa de conversión | 🔴 | Analytics | Queries sobre datos existentes + vistas. |
| RF-72 | Métricas por oferta: vistas, postulaciones, tasa de conversión, tiempo promedio en pipeline | 🟡 | Analytics | Requiere tracking de vistas (tabla `job_views`). |
| RF-73 | Exportar postulaciones y candidatos en CSV | 🟡 | Analytics | Endpoint de API. Limitado por plan. |
| RF-74 | Métricas de calidad de ofertas (completitud, inclusividad score del linter) | 🟢 | Analytics | Derivado del linter RF-32. |

#### Bounded Context: Administration (extendido)

| ID | Requisito | Prioridad | BC | Observaciones |
|----|-----------|-----------|-----|---------------|
| RF-75 | Gestionar catálogo maestro de skills (CRUD + sinónimos + relaciones) | 🔴 | Administration | Panel admin. |
| RF-76 | Gestionar planes de suscripción | 🔴 | Administration | Panel admin. |
| RF-77 | Ver log de auditoría de acciones administrativas | 🔴 | Administration | Tabla `audit_log`. |
| RF-78 | Gestionar artículos del help center | 🟡 | Administration | Panel admin. |
| RF-79 | Gestionar flujos de onboarding | 🟡 | Administration | Panel admin. |

---

## 2. Requisitos No Funcionales — Nuevos

| ID | Categoría | Requisito | Prioridad |
|----|-----------|-----------|-----------|
| RNF-14 | Usabilidad | API debe separar claramente endpoints públicos (`/api/v1/public/**`) de endpoints de dashboard (`/api/v1/dashboard/**`) y admin (`/api/v1/admin/**`) | 🔴 |
| RNF-15 | Accesibilidad | Respuestas de API incluyen mensajes de error descriptivos y localizables (código + mensaje) | 🟡 |
| RNF-16 | Explicabilidad | Todo match score debe acompañarse de una explicación estructurada en JSON que el frontend pueda renderizar | 🔴 |
| RNF-17 | Seguridad por roles | Endpoints de dashboard requieren rol EMPLOYER o RECRUITER; endpoints admin requieren ADMIN; moderación requiere MODERATOR | 🔴 |
| RNF-18 | Auditoría | Toda acción administrativa (ban, moderación, cambio de plan, cambio de rol) se registra en `audit_log` con actor, acción, target y timestamp | 🔴 |
| RNF-19 | Rendimiento | Búsqueda de ofertas con filtros combinados debe responder en < 500ms para 100K ofertas | 🟡 |
| RNF-20 | Rendimiento | Cálculo de match score para un candidato vs una oferta debe completarse en < 200ms | 🟡 |
| RNF-21 | Observabilidad | Logs estructurados (JSON) con correlation ID por request | 🟡 |
| RNF-22 | Observabilidad | Métricas de salud expuestas en `/actuator/health` y `/actuator/prometheus` | 🟢 |
| RNF-23 | Consistencia | Skills siempre referenciados por `skill_id` FK al catálogo maestro; nunca strings libres en tablas nuevas | 🔴 |
| RNF-24 | Versionado | Toda migración de esquema es aditiva; columnas deprecated se marcan y eliminan en migración posterior con ventana de 2 releases | 🔴 |
| RNF-25 | Seguridad | Rate limiting en endpoints públicos de autenticación y búsqueda | 🟡 |
| RNF-26 | Seguridad | Datos sensibles (salario deseado del candidato) solo visibles para el propio candidato y reclutadores con suscripción activa | 🔴 |

---

## 3. Bounded Contexts — Actualizados

| Bounded Context | Estado | Paquete | Justificación |
|----------------|--------|---------|---------------|
| **Authentication** | Implementado (parcial) | `com.ITJobsBackend.authentication` | Sin cambios. |
| **Jobs** | Implementado (parcial) | `com.ITJobsBackend.jobs` | Se extiende con `employer_id`, featured, expires_at, linter. |
| **Shared** | Implementado | `com.ITJobsBackend.shared` | Se extiende con SkillId VO y audit infrastructure. |
| **Applications** | Pendiente (DB creada) | `com.ITJobsBackend.applications` | Se extiende con pipeline, notas, match score, historial. |
| **Profiles** | Pendiente (DB creada) | `com.ITJobsBackend.profiles` | Se extiende con campos para matching. |
| **Administration** | Pendiente | `com.ITJobsBackend.administration` | Se extiende con gestión de skills, planes, audit log. |
| **Matching** ⭐ NEW | Pendiente | `com.ITJobsBackend.matching` | **Motor de matching explicable**. Catálogo de skills, sinónimos, relaciones, algoritmo de scoring. Justificación: la lógica de matching es compleja, tiene su propio lenguaje ubicuo (exact match, transferable, partial, emergent) y no pertenece a Jobs ni Applications. |
| **Moderation** ⭐ EXTENDED | Pendiente | `com.ITJobsBackend.moderation` | Se extiende significativamente con reportes, decisiones, apelaciones. Ya estaba previsto. |
| **Billing** ⭐ NEW | Pendiente | `com.ITJobsBackend.billing` | **Monetización B2B**. Planes, suscripciones, pagos, límites. Justificación: tiene su propio ciclo de vida y invariantes (no puedes publicar si excedes tu plan). |
| **Analytics** ⭐ NEW | Pendiente | `com.ITJobsBackend.analytics` | **Read-model para métricas**. Vistas de ofertas, conversiones, dashboards. Justificación: es un contexto de lectura puro que consume eventos de otros BCs. |
| **Onboarding** | NO se crea como BC | — | Demasiado simple para un BC propio. Se implementa como módulo dentro de **Administration** (configuración) y **Shared** (progreso del usuario). |
| **Help Center** | NO se crea como BC | — | Demasiado simple. Se implementa como módulo dentro de **Administration**. |

### Mapa de contexto (relaciones entre BCs)

```
Authentication ──publishes──▶ UserRegisteredEvent ──▶ Profiles (crea perfil vacío)
                                                  ──▶ Onboarding (inicia flujo)

Jobs ──publishes──▶ JobCreatedEvent ──▶ Analytics (registra publicación)
     ──consumes──◀ Billing (valida límites antes de publicar)
     ──consumes──◀ Matching (catálogo de skills para job_skills)

Applications ──publishes──▶ ApplicationCreatedEvent ──▶ Matching (calcula score)
             ──publishes──▶ ApplicationStageChangedEvent ──▶ Analytics

Matching ──consumes──◀ Profiles (skills del candidato)
         ──consumes──◀ Jobs (skills de la oferta)

Moderation ──consumes──◀ Jobs (desactiva oferta reportada)
           ──publishes──▶ ContentModeratedEvent ──▶ Analytics

Billing ──publishes──▶ SubscriptionActivatedEvent ──▶ Jobs (desbloquea features)
```

---

## 4. Modelo de Datos — Cambios Propuestos

### 4.1 Tablas existentes a modificar

#### `jobs` — Agregar FK a employer y campos de promoción

| Columna | Cambio | Prioridad | Notas |
|---------|--------|-----------|-------|
| `employer_id CHAR(36)` | ADD, FK → employers(id) | 🔴 | NULLABLE inicialmente para migración. `company` se mantiene por compatibilidad. |
| `featured BOOLEAN DEFAULT FALSE` | ADD | 🔴 | Oferta destacada. |
| `featured_until DATETIME` | ADD | 🔴 | Expiración de la promoción. |
| `expires_at DATETIME` | ADD | 🟡 | Expiración automática de la oferta. |
| `inclusivity_score SMALLINT` | ADD | 🟢 | Score del linter (0-100). |
| `company` | KEEP (deprecated) | — | Se mantiene temporalmente. Nuevas ofertas deben usar employer_id. |

#### `job_skills` — Migrar a skills por referencia + nivel de requisito

| Columna | Cambio | Prioridad | Notas |
|---------|--------|-----------|-------|
| `skill_id CHAR(36)` | ADD, FK → skills(id) | 🔴 | Reemplaza `skill` string. |
| `requirement_level VARCHAR(20)` | ADD, DEFAULT 'REQUIRED' | 🔴 | REQUIRED, DESIRED, OPTIONAL. |
| `skill` | KEEP (deprecated) | — | Se mantiene hasta migración de datos completada. |

#### `candidate_skills` — Migrar a skills por referencia + nivel + evidencia

| Columna | Cambio | Prioridad | Notas |
|---------|--------|-----------|-------|
| `skill_id CHAR(36)` | ADD, FK → skills(id) | 🔴 | Reemplaza `skill` string. |
| `proficiency_level VARCHAR(20)` | ADD, DEFAULT 'INTERMEDIATE' | 🔴 | BEGINNER, INTERMEDIATE, ADVANCED, EXPERT. |
| `years_experience SMALLINT` | ADD | 🟡 | Años usando el skill. |
| `skill` | KEEP (deprecated) | — | Se mantiene hasta migración de datos. |

#### `candidates` — Enriquecer perfil para matching

| Columna | Cambio | Prioridad |
|---------|--------|-----------|
| `years_of_experience SMALLINT` | ADD | 🔴 |
| `desired_salary_min DECIMAL(15,2)` | ADD | 🟡 |
| `desired_salary_max DECIMAL(15,2)` | ADD | 🟡 |
| `desired_employment_type VARCHAR(20)` | ADD | 🟡 |
| `open_to_remote BOOLEAN DEFAULT FALSE` | ADD | 🟡 |

#### `employers` — Enriquecer perfil de empresa

| Columna | Cambio | Prioridad |
|---------|--------|-----------|
| `logo_url VARCHAR(500)` | ADD | 🟡 |
| `description TEXT` | ADD | 🟡 |
| `company_size VARCHAR(20)` | ADD | 🟢 |

#### `applications` — Pipeline extendido + matching

| Columna | Cambio | Prioridad |
|---------|--------|-----------|
| `match_score SMALLINT` | ADD | 🔴 |
| `match_explanation JSON` | ADD | 🔴 |
| `match_category VARCHAR(30)` | ADD | 🔴 |
| `status` | Valores extendidos: PENDING, SCREENING, INTERVIEW, OFFER, ACCEPTED, REJECTED, WITHDRAWN | 🔴 |

#### `users` — Moderación

| Columna | Cambio | Prioridad |
|---------|--------|-----------|
| `banned_until DATETIME` | ADD | 🟡 |
| `ban_reason VARCHAR(500)` | ADD | 🟡 |

### 4.2 Tablas nuevas

#### Matching — Catálogo de skills

```sql
-- Catálogo maestro de skills
CREATE TABLE skills (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50),          -- 'LANGUAGE', 'FRAMEWORK', 'DATABASE', 'TOOL', 'SOFT_SKILL', etc.
    created_at TIMESTAMP NOT NULL
);

-- Sinónimos de skills (ej: "JS" → "JavaScript")
CREATE TABLE skill_synonyms (
    id CHAR(36) PRIMARY KEY,
    skill_id CHAR(36) NOT NULL,
    synonym VARCHAR(100) NOT NULL UNIQUE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
);

-- Relaciones entre skills (equivalentes, adyacentes)
CREATE TABLE skill_relationships (
    id CHAR(36) PRIMARY KEY,
    skill_id_from CHAR(36) NOT NULL,
    skill_id_to CHAR(36) NOT NULL,
    relationship_type VARCHAR(20) NOT NULL, -- 'EQUIVALENT', 'ADJACENT', 'PARENT', 'CHILD'
    weight DECIMAL(3,2) DEFAULT 1.00,       -- 1.0 = equivalente, 0.7 = adyacente, etc.
    FOREIGN KEY (skill_id_from) REFERENCES skills(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id_to) REFERENCES skills(id) ON DELETE CASCADE,
    UNIQUE (skill_id_from, skill_id_to, relationship_type)
);
```

#### Applications — Notas e historial

```sql
-- Notas del reclutador en una postulación
CREATE TABLE application_notes (
    id CHAR(36) PRIMARY KEY,
    application_id CHAR(36) NOT NULL,
    author_id CHAR(36) NOT NULL,        -- recruiter/employer user id
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(id)
);

-- Historial de cambios de estado
CREATE TABLE application_status_history (
    id CHAR(36) PRIMARY KEY,
    application_id CHAR(36) NOT NULL,
    from_status VARCHAR(20),
    to_status VARCHAR(20) NOT NULL,
    changed_by CHAR(36) NOT NULL,
    changed_at TIMESTAMP NOT NULL,
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES users(id)
);
```

#### Moderation

```sql
-- Reportes de contenido
CREATE TABLE reports (
    id CHAR(36) PRIMARY KEY,
    reporter_id CHAR(36) NOT NULL,
    target_type VARCHAR(20) NOT NULL,     -- 'JOB', 'USER', 'APPLICATION'
    target_id CHAR(36) NOT NULL,
    reason VARCHAR(30) NOT NULL,           -- 'SPAM', 'MISLEADING', 'DISCRIMINATORY', 'IMMORAL', 'DUPLICATE', 'OTHER'
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'RESOLVED', 'DISMISSED'
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (reporter_id) REFERENCES users(id)
);
CREATE INDEX idx_reports_target ON reports(target_type, target_id);
CREATE INDEX idx_reports_status ON reports(status);

-- Decisiones de moderación
CREATE TABLE moderation_decisions (
    id CHAR(36) PRIMARY KEY,
    report_id CHAR(36) NOT NULL,
    moderator_id CHAR(36) NOT NULL,
    decision VARCHAR(20) NOT NULL,        -- 'APPROVED', 'REJECTED', 'ESCALATED'
    reason VARCHAR(500) NOT NULL,
    action_taken VARCHAR(30),              -- 'CONTENT_REMOVED', 'USER_WARNED', 'USER_BANNED', 'NONE'
    decided_at TIMESTAMP NOT NULL,
    FOREIGN KEY (report_id) REFERENCES reports(id),
    FOREIGN KEY (moderator_id) REFERENCES users(id)
);

-- Apelaciones
CREATE TABLE moderation_appeals (
    id CHAR(36) PRIMARY KEY,
    decision_id CHAR(36) NOT NULL,
    appellant_id CHAR(36) NOT NULL,
    justification TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'ACCEPTED', 'REJECTED'
    reviewed_by CHAR(36),
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (decision_id) REFERENCES moderation_decisions(id),
    FOREIGN KEY (appellant_id) REFERENCES users(id),
    FOREIGN KEY (reviewed_by) REFERENCES users(id)
);
```

#### Billing

```sql
-- Planes de suscripción
CREATE TABLE subscription_plans (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,        -- 'FREE', 'STARTER', 'PROFESSIONAL', 'ENTERPRISE'
    price_monthly DECIMAL(10,2) NOT NULL,
    price_yearly DECIMAL(10,2),
    max_active_jobs INT NOT NULL,
    max_featured_jobs INT NOT NULL DEFAULT 0,
    can_export BOOLEAN NOT NULL DEFAULT FALSE,
    can_see_match_details BOOLEAN NOT NULL DEFAULT FALSE,
    can_add_recruiter_notes BOOLEAN NOT NULL DEFAULT TRUE,
    max_recruiters INT NOT NULL DEFAULT 1,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Suscripciones activas de empleadores
CREATE TABLE subscriptions (
    id CHAR(36) PRIMARY KEY,
    employer_id CHAR(36) NOT NULL,
    plan_id CHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL,              -- 'ACTIVE', 'CANCELLED', 'EXPIRED', 'PAST_DUE'
    billing_cycle VARCHAR(10) NOT NULL,       -- 'MONTHLY', 'YEARLY'
    started_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (employer_id) REFERENCES employers(id),
    FOREIGN KEY (plan_id) REFERENCES subscription_plans(id)
);
CREATE INDEX idx_subscriptions_employer ON subscriptions(employer_id);

-- Pagos
CREATE TABLE payments (
    id CHAR(36) PRIMARY KEY,
    subscription_id CHAR(36) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    status VARCHAR(20) NOT NULL,              -- 'PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'
    payment_method VARCHAR(30),
    external_reference VARCHAR(255),          -- referencia de pasarela externa
    paid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id)
);
```

#### Analytics — Tracking

```sql
-- Vistas de ofertas (para métricas de conversión)
CREATE TABLE job_views (
    id CHAR(36) PRIMARY KEY,
    job_id CHAR(36) NOT NULL,
    viewer_id CHAR(36),                       -- NULL si es anónimo
    viewed_at TIMESTAMP NOT NULL,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (viewer_id) REFERENCES users(id)
);
CREATE INDEX idx_job_views_job ON job_views(job_id);
CREATE INDEX idx_job_views_date ON job_views(viewed_at);
```

#### Auditoría

```sql
-- Log de auditoría para acciones administrativas
CREATE TABLE audit_log (
    id CHAR(36) PRIMARY KEY,
    actor_id CHAR(36) NOT NULL,
    action VARCHAR(50) NOT NULL,             -- 'USER_BANNED', 'ROLE_CHANGED', 'PLAN_CHANGED', 'CONTENT_MODERATED', etc.
    target_type VARCHAR(30) NOT NULL,
    target_id CHAR(36) NOT NULL,
    details JSON,
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (actor_id) REFERENCES users(id)
);
CREATE INDEX idx_audit_log_actor ON audit_log(actor_id);
CREATE INDEX idx_audit_log_target ON audit_log(target_type, target_id);
CREATE INDEX idx_audit_log_date ON audit_log(created_at);
```

#### Onboarding

```sql
-- Flujos de onboarding configurables
CREATE TABLE onboarding_flows (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    target_role VARCHAR(50) NOT NULL,         -- 'CANDIDATE', 'EMPLOYER', 'RECRUITER'
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL
);

-- Pasos de un flujo
CREATE TABLE onboarding_steps (
    id CHAR(36) PRIMARY KEY,
    flow_id CHAR(36) NOT NULL,
    step_order INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    target_element VARCHAR(200),              -- CSS selector o route para el frontend
    step_type VARCHAR(20) NOT NULL,           -- 'TOOLTIP', 'MODAL', 'HIGHLIGHT', 'VIDEO'
    video_url VARCHAR(500),
    FOREIGN KEY (flow_id) REFERENCES onboarding_flows(id) ON DELETE CASCADE
);

-- Progreso del usuario
CREATE TABLE user_onboarding_progress (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    flow_id CHAR(36) NOT NULL,
    last_completed_step INT NOT NULL DEFAULT 0,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (flow_id) REFERENCES onboarding_flows(id) ON DELETE CASCADE,
    UNIQUE (user_id, flow_id)
);
```

#### Help Center

```sql
CREATE TABLE help_categories (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    sort_order INT NOT NULL DEFAULT 0
);

CREATE TABLE help_articles (
    id CHAR(36) PRIMARY KEY,
    category_id CHAR(36) NOT NULL,
    title VARCHAR(300) NOT NULL,
    content TEXT NOT NULL,
    video_url VARCHAR(500),
    target_roles VARCHAR(200),                -- comma-separated: 'CANDIDATE,EMPLOYER,RECRUITER'
    published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (category_id) REFERENCES help_categories(id)
);
CREATE FULLTEXT INDEX idx_help_articles_search ON help_articles(title, content);
```

#### Job Feedback (estructurado)

```sql
CREATE TABLE job_feedback (
    id CHAR(36) PRIMARY KEY,
    job_id CHAR(36) NOT NULL,
    candidate_id CHAR(36) NOT NULL,
    accuracy_rating SMALLINT NOT NULL,        -- 1-5: ¿La oferta reflejaba el puesto real?
    transparency_rating SMALLINT NOT NULL,    -- 1-5: ¿Información clara y completa?
    process_rating SMALLINT NOT NULL,         -- 1-5: ¿Proceso de selección justo?
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (job_id) REFERENCES jobs(id),
    FOREIGN KEY (candidate_id) REFERENCES candidates(id),
    UNIQUE (job_id, candidate_id)
);
```

---

## 5. Migraciones Flyway Propuestas

| Migración | Contenido | Prioridad | Notas |
|-----------|-----------|-----------|-------|
| **V11** | Tabla `skills` (catálogo maestro), `skill_synonyms`, `skill_relationships` | 🔴 | Base para todo el matching. |
| **V12** | ALTER `job_skills`: ADD `skill_id`, `requirement_level`. ALTER `candidate_skills`: ADD `skill_id`, `proficiency_level`, `years_experience`. | 🔴 | Columnas `skill` antiguas se mantienen. Script de migración de datos: INSERT INTO skills(id,name) SELECT DISTINCT UUID(), skill FROM job_skills; UPDATE job_skills js SET skill_id = (SELECT id FROM skills WHERE name = js.skill); |
| **V13** | ALTER `jobs`: ADD `employer_id`, `featured`, `featured_until`, `expires_at`. INDEX en `employer_id`. | 🔴 | `employer_id` NULLABLE. Script: UPDATE jobs j SET employer_id = (SELECT id FROM employers WHERE company_name = j.company); |
| **V14** | ALTER `candidates`: ADD `years_of_experience`, `desired_salary_min`, `desired_salary_max`, `desired_employment_type`, `open_to_remote` | 🔴 | Todos NULLABLE. |
| **V15** | ALTER `applications`: ADD `match_score`, `match_explanation`, `match_category`. Extender valores de `status`. | 🔴 | JSON column para match_explanation. |
| **V16** | Tablas `application_notes`, `application_status_history` | 🔴 | Pipeline y trazabilidad. |
| **V17** | Tablas `reports`, `moderation_decisions`, `moderation_appeals` | 🔴 | Moderación completa. |
| **V18** | ALTER `users`: ADD `banned_until`, `ban_reason` | 🟡 | Moderación de usuarios. |
| **V19** | Tablas `subscription_plans`, `subscriptions`, `payments` | 🔴 | Monetización. |
| **V20** | ALTER `employers`: ADD `logo_url`, `description`, `company_size` | 🟡 | Perfil enriquecido. |
| **V21** | Tabla `audit_log` | 🔴 | Auditoría. |
| **V22** | Tabla `job_views` | 🟡 | Analytics. |
| **V23** | Tablas `onboarding_flows`, `onboarding_steps`, `user_onboarding_progress` | 🟡 | Onboarding. |
| **V24** | Tablas `help_categories`, `help_articles` | 🟡 | Help center. |
| **V25** | Tabla `job_feedback` | 🟢 | Feedback estructurado. |
| **V26** | Tabla `candidate_projects` (id, candidate_id, title, description, url, skill_ids JSON, created_at) | 🟡 | Evidencia de skills. |
| **V27** | DROP COLUMN `job_skills.skill`, `candidate_skills.skill` (solo después de verificar migración completa a skill_id) | 🟢 | Limpieza. Ejecutar mínimo 2 releases después de V12. |
| **V28** | ALTER `jobs`: SET `employer_id` NOT NULL (solo después de migración completa de datos) | 🟡 | Ejecutar después de verificar que todas las ofertas tienen employer_id. |

### Estrategia de transición para columnas deprecated

1. **`job_skills.skill`** y **`candidate_skills.skill`**: se mantienen junto a `skill_id` hasta V27. Código nuevo usa solo `skill_id`. Código legacy puede leer ambos.
2. **`jobs.company`**: se mantiene indefinidamente como campo desnormalizado de lectura. `employer_id` es la referencia canónica.

---

## 6. UI Pública vs Dashboard Privado — Observaciones para API

### Estructura de endpoints propuesta

```
/api/v1/public/                    ← Sin autenticación
├── jobs                            GET (búsqueda), GET /{id} (detalle)
├── jobs/{id}/skills                GET
├── categories                      GET
├── auth/register                   POST
├── auth/login                      POST
├── auth/login/google               POST
├── auth/verify-email               POST
└── help/articles                   GET, GET /{id}

/api/v1/candidate/                 ← Requiere ROLE_CANDIDATE
├── profile                         GET, PUT
├── skills                          GET, POST, DELETE
├── projects                        GET, POST, PUT, DELETE
├── applications                    GET, POST (postularse)
├── applications/{id}/withdraw      POST
├── saved-jobs                      GET, POST, DELETE
├── match/suggestions               GET (ofertas sugeridas)
└── onboarding/progress             GET, PUT

/api/v1/dashboard/                 ← Requiere ROLE_EMPLOYER o ROLE_RECRUITER
├── jobs                            GET, POST, PUT (CRUD de ofertas propias)
├── jobs/{id}/applications          GET (postulaciones recibidas)
├── jobs/{id}/applications/{id}/notes  GET, POST
├── jobs/{id}/applications/{id}/stage  PUT (mover en pipeline)
├── jobs/{id}/analytics             GET (métricas por oferta)
├── analytics/overview              GET (dashboard general)
├── export/applications             GET (CSV)
├── subscription                    GET, POST (ver/cambiar plan)
├── recruiters                      GET, POST, DELETE (gestionar reclutadores)
└── onboarding/progress             GET, PUT

/api/v1/moderation/                ← Requiere ROLE_MODERATOR
├── reports                         GET (pendientes)
├── reports/{id}/decide             POST
├── appeals                         GET (pendientes)
└── appeals/{id}/review             POST

/api/v1/admin/                     ← Requiere ROLE_ADMIN
├── users                           GET, PUT (gestionar)
├── skills                          CRUD
├── skills/{id}/synonyms            CRUD
├── skills/{id}/relationships       CRUD
├── categories                      CRUD
├── subscription-plans              CRUD
├── onboarding/flows                CRUD
├── help/articles                   CRUD
├── help/categories                 CRUD
└── audit-log                       GET
```

> **Nota**: Los endpoints existentes (`/api/v1/auth/**`, `/api/v1/jobs/**`) se mantienen por compatibilidad pero se deprecan progresivamente en favor de la estructura `/api/v1/public/**`.

---

## 7. Riesgos y Decisiones de Diseño

| # | Tema | Decisión | Riesgo si no se aborda |
|---|------|----------|----------------------|
| 1 | **Skills como strings** | Migrar a catálogo normalizado con FK. Mantener columna string temporalmente. | Matching imposible: no hay sinónimos, no hay relaciones, no hay niveles. |
| 2 | **jobs.company sin FK** | Agregar `employer_id` FK. Mantener `company` como campo desnormalizado. | No se puede vincular ofertas a empleadores, no hay dashboard, no hay analítica por empresa. |
| 3 | **Pipeline simplista** | Extender estados de `applications`. Agregar historial. | Los reclutadores no pueden gestionar un proceso real de selección. |
| 4 | **Match score en application** | Almacenar score + explicación al momento de la postulación. | Si se calcula on-the-fly, es lento y no reproducible. |
| 5 | **Billing como BC separado** | Separar de Administration. | Mezclar lógica de facturación con admin genera acoplamiento y dificulta testing. |
| 6 | **Matching como BC separado** | Separar de Jobs y Applications. | La lógica de matching es la ventaja competitiva principal; merece su propio modelo. |
| 7 | **Onboarding no es BC** | Implementar como módulo de Administration + tablas compartidas. | Crear un BC para tooltips y tutoriales es over-engineering. |
| 8 | **JSON para match_explanation** | Usar tipo JSON de MySQL para la explicación del match. | Alternativa: TEXT con JSON serializado. JSON nativo permite queries. |
| 9 | **Audit log genérico** | Una tabla `audit_log` con `action` y `details` JSON. | Alternativa: una tabla por tipo de evento. Genérico es más simple y extensible. |
| 10 | **Rate limiting** | Implementar con bucket4j o Spring Cloud Gateway. No requiere tabla. | Sin rate limiting, endpoints públicos son vulnerables a abuso. |
| 11 | **Migración de datos de skills** | V12 migra strings existentes al catálogo. Script SQL incluido. | Si no se migran los datos existentes, se pierden skills de ofertas y candidatos ya creados. |
| 12 | **Compatibilidad de endpoints** | Mantener endpoints viejos funcionando durante transición. | Romper contratos de API impide al frontend funcionar durante la migración. |

---

## Apéndice: Resumen de tablas por bounded context

| BC | Tablas propias | Tablas compartidas |
|----|---------------|-------------------|
| Authentication | `users`, `user_roles` | — |
| Profiles | `candidates`, `candidate_skills`, `employers`, `recruiters`, `candidate_projects` | `users`, `skills` |
| Jobs | `jobs`, `job_skills`, `job_categories`, `job_category_mapping` | `employers`, `skills` |
| Applications | `applications`, `application_notes`, `application_status_history`, `saved_jobs` | `jobs`, `candidates` |
| Matching | `skills`, `skill_synonyms`, `skill_relationships` | `job_skills`, `candidate_skills` |
| Moderation | `reports`, `moderation_decisions`, `moderation_appeals`, `job_feedback` | `users`, `jobs` |
| Billing | `subscription_plans`, `subscriptions`, `payments` | `employers` |
| Analytics | `job_views` | Lee de `jobs`, `applications`, `users` |
| Administration | `audit_log`, `onboarding_flows`, `onboarding_steps`, `user_onboarding_progress`, `help_categories`, `help_articles` | `users`, `skills` |

