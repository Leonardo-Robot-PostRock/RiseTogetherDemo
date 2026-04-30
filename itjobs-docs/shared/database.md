## **Documentación de la Base de Datos para el Sistema de Búsqueda de Empleo**

> **Nota:** Todos los IDs usan UUID (`CHAR(36)`). El esquema está versionado con Flyway (V1–V21).

---

## Migraciones Flyway

| Migración | Contenido                                            |
|-----------|------------------------------------------------------|
| V1        | `users`, `user_roles`, `jobs`, `job_skills`          |
| V2        | Fix `job_skills` (recrear para `@ElementCollection`) |
| V3        | `employers`, `candidates`, `candidate_skills`        |
| V4        | `applications`, `saved_jobs`                         |
| V5        | `job_categories`, `job_category_mapping`             |
| V6        | `recruiters`                                         |
| V7        | `salary_min`, `salary_max` de DOUBLE a DECIMAL(15,2) |
| V8        | `salary_min`, `salary_max` NOT NULL (transparencia)  |
| V9        | Agregar `google_sub` a `users`                       |
| V10       | Agregar `verification_token` y expiración a `users`  |
| V11       | Agregar `employer_id CHAR(36)` FK a `jobs` → `employers` |
| V12       | Agregar `company_size VARCHAR(20)` a `employers`     |
| V13       | Agregar `logo_url VARCHAR(500)` y `description TEXT` a `employers` |
| V14       | Agregar `terms_version` y `terms_accepted_at` a `users` *(reemplazado por V15)* |
| V15       | Crear `terms_documents` y `user_terms_acceptances`; migrar datos de V14; eliminar columnas de `users` |
| V16       | Agregar `employer_type` a `employers` (HR_AGENCY, SOFTWARE_HOUSE, CONSULTING_FIRM, STARTUP, OTHER) |
| V17       | Agregar `created_by_user_id`, `posted_on_behalf_of_employer_id`, `job_type`, `remote_allowed`, `expires_at`, `featured`, `featured_until` a `jobs` |
| V18       | Crear `recruiter_employer_associations` (recruiter ↔ employers, many-to-many) |
| V19       | Extender `candidates` con `years_of_experience`, `desired_salary_min/max`, `desired_employment_type`, `open_to_remote`, `available_for_freelance` |
| V20       | Crear `freelancer_profiles` (perfil público de freelancer vinculado a usuario) |
| V21       | Agregar `work_modality` a `jobs` (`REMOTE`, `HYBRID`, `ON_SITE`) |

---

## V1 — Autenticación y Jobs

### **1. Tabla `users`**

Almacena la información de autenticación de los usuarios.

- **id** (PK): UUID del usuario.
- **username**: Nombre de usuario (único).
- **email**: Correo electrónico (único).
- **password**: Contraseña (hasheada con BCrypt).
- **active**: Si el usuario está activo.
- **email_verified**: Si el email fue verificado.
- **google_sub**: ID de cuenta de Google (único) añadido en V9.
- **verification_token**: Token para verificar email (V10).
- **verification_token_expires_at**: Expiración del token (V10).
- **created_at**: Fecha de creación.
- **updated_at**: Fecha de última actualización.

```sql
CREATE TABLE users (
    id CHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    google_sub VARCHAR(255) UNIQUE, -- V9: add google_sub
    verification_token VARCHAR(255), -- V10: add verification_token
    verification_token_expires_at DATETIME(3), -- V10: token expiration
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_username ON users(username);
```

### **2. Tabla `user_roles`**

Roles asignados a cada usuario. Usa `@ElementCollection` en JPA (no una tabla `roles` separada).

- **user_id** (FK, PK): Referencia al usuario (`users.id`).
- **role** (PK): Nombre del rol (ej. `ROLE_USER`, `ROLE_ADMIN`, `ROLE_EMPLOYER`).

```sql
CREATE TABLE user_roles (
    user_id CHAR(36) NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### **3. Tabla `jobs`**

Ofertas de empleo publicadas.

- **id** (PK): UUID del trabajo.
- **title**: Título del puesto.
- **description**: Descripción detallada.
- **company**: Nombre de la empresa.
- **location**: Ubicación del trabajo (**obligatorio**).
- **salary_min** / **salary_max**: Rango salarial (**obligatorio**, DECIMAL(15,2)).
- **currency**: Moneda del salario.
- **employment_type**: Tipo de empleo (`FULL_TIME`, `PART_TIME`, `CONTRACT`, `FREELANCE`, `INTERNSHIP`).
- **work_modality**: Modalidad de trabajo (`REMOTE`, `HYBRID`, `ON_SITE`). Añadido en V21.
- **status**: Estado (`OPEN`, `CLOSED`, `DRAFT`, `EXPIRED`, `INACTIVE`).
- **employer_id** (FK, nullable): Referencia al empleador (`employers.id`). Añadido en V11.
- **created_at**: Fecha de creación.
- **updated_at**: Fecha de última actualización.

```sql
CREATE TABLE jobs (
    id CHAR(36) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    company VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    salary_min DECIMAL(15,2) NOT NULL,  -- V7+V8: DECIMAL NOT NULL
    salary_max DECIMAL(15,2) NOT NULL,
    currency VARCHAR(10),
    employment_type VARCHAR(20) NOT NULL,
    work_modality   VARCHAR(20) NOT NULL DEFAULT 'ON_SITE', -- V21: REMOTE | HYBRID | ON_SITE
    status VARCHAR(20) NOT NULL,        -- OPEN, CLOSED, DRAFT, EXPIRED, INACTIVE
    employer_id CHAR(36),               -- V11: FK → employers
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (employer_id) REFERENCES employers(id) ON DELETE SET NULL
);

CREATE INDEX idx_job_title ON jobs(title);
CREATE INDEX idx_job_company ON jobs(company);
CREATE INDEX idx_job_employer ON jobs(employer_id);  -- V11
```

### **4. Tabla `job_skills`**

Skills requeridos por un trabajo. Usa `@ElementCollection` en JPA (skills como strings simples).

- **job_id** (FK, PK): Referencia al trabajo (`jobs.id`).
- **skill** (PK): Nombre del skill (ej. `Java`, `Spring Boot`).

```sql
CREATE TABLE job_skills (
    job_id CHAR(36) NOT NULL,
    skill VARCHAR(100) NOT NULL,
    PRIMARY KEY (job_id, skill),
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE
);
```

---

## V3 — Perfiles (Employers + Candidates)

### **5. Tabla `employers`**

Perfil de empresa/empleador vinculado a un usuario.

- **id** (PK, FK): UUID del usuario (`users.id`). CASCADE al eliminar.
- **company_name**: Nombre de la empresa.
- **industry**: Sector.
- **website**: Sitio web.
- **location**: Ubicación.
- **contact_person**: Persona de contacto.
- **contact_email**: Email de contacto.
- **company_size**: Tamaño de la empresa (ej. `SMALL`, `MEDIUM`, `LARGE`). Añadido en V12.
- **logo_url**: URL del logo de la empresa. Añadido en V13.
- **description**: Descripción de la empresa. Añadido en V13.
- **created_at** / **updated_at**: Timestamps.

```sql
CREATE TABLE employers (
    id CHAR(36) PRIMARY KEY,
    company_name VARCHAR(100) NOT NULL,
    industry VARCHAR(50),
    website VARCHAR(255),
    location VARCHAR(100),
    contact_person VARCHAR(100),
    contact_email VARCHAR(255),
    company_size VARCHAR(20),     -- V12
    logo_url VARCHAR(500),        -- V13
    description TEXT,             -- V13
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);
```

### **6. Tabla `candidates`**

Perfil de candidato vinculado a un usuario.

- **id** (PK, FK): UUID del usuario (`users.id`). CASCADE al eliminar.
- **first_name** / **last_name**: Nombre completo.
- **resume**: Enlace al CV.
- **profile_summary**: Resumen del perfil.
- **location**: Ubicación.
- **created_at** / **updated_at**: Timestamps.

```sql
CREATE TABLE candidates (
    id CHAR(36) PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    resume VARCHAR(255),
    profile_summary TEXT,
    location VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);
```

### **7. Tabla `candidate_skills`**

Skills del candidato. Usa `@ElementCollection` (misma estrategia que `job_skills`).

- **candidate_id** (FK, PK): Referencia al candidato (`candidates.id`). CASCADE al eliminar.
- **skill** (PK): Nombre del skill.

```sql
CREATE TABLE candidate_skills (
    candidate_id CHAR(36) NOT NULL,
    skill VARCHAR(100) NOT NULL,
    PRIMARY KEY (candidate_id, skill),
    FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE
);
```

---

## V4 — Postulaciones (Applications + Saved Jobs)

### **8. Tabla `applications`**

Postulaciones de candidatos a ofertas de empleo.

- **id** (PK): UUID de la postulación.
- **job_id** (FK): Referencia al trabajo (`jobs.id`). Sin CASCADE (preservar historial).
- **candidate_id** (FK): Referencia al candidato (`candidates.id`). Sin CASCADE.
- **application_date**: Fecha de postulación.
- **status**: Estado (`PENDING`, `REVIEWED`, `ACCEPTED`, `REJECTED`).
- **resume**: CV usado en esta postulación.
- **cover_letter**: Carta de presentación.

```sql
CREATE TABLE applications (
    id CHAR(36) PRIMARY KEY,
    job_id CHAR(36) NOT NULL,
    candidate_id CHAR(36) NOT NULL,
    application_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    resume VARCHAR(255),
    cover_letter TEXT,
    FOREIGN KEY (job_id) REFERENCES jobs(id),
    FOREIGN KEY (candidate_id) REFERENCES candidates(id)
);

CREATE INDEX idx_application_candidate ON applications(candidate_id);
CREATE INDEX idx_application_job ON applications(job_id);
CREATE INDEX idx_application_status ON applications(status);
```

### **9. Tabla `saved_jobs`**

Ofertas guardadas por los candidatos para revisar después.

- **id** (PK): UUID.
- **candidate_id** (FK): Candidato (`candidates.id`). CASCADE al eliminar.
- **job_id** (FK): Oferta (`jobs.id`). CASCADE al eliminar.
- **saved_at**: Fecha de guardado.
- UNIQUE constraint en `(candidate_id, job_id)` para evitar duplicados.

```sql
CREATE TABLE saved_jobs (
    id CHAR(36) PRIMARY KEY,
    candidate_id CHAR(36) NOT NULL,
    job_id CHAR(36) NOT NULL,
    saved_at TIMESTAMP NOT NULL,
    FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    UNIQUE (candidate_id, job_id)
);

CREATE INDEX idx_saved_jobs_candidate ON saved_jobs(candidate_id);
```

---

## V5 — Categorías

### **10. Tabla `job_categories`**

Categorías de trabajo (ej. `Tecnología`, `Salud`, `Finanzas`).

```sql
CREATE TABLE job_categories (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);
```

### **11. Tabla `job_category_mapping`**

Relación muchos-a-muchos entre ofertas y categorías.

```sql
CREATE TABLE job_category_mapping (
    job_id CHAR(36) NOT NULL,
    category_id CHAR(36) NOT NULL,
    PRIMARY KEY (job_id, category_id),
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES job_categories(id) ON DELETE CASCADE
);
```

---

## V6 — Reclutadores

### **12. Tabla `recruiters`**

Reclutadores asociados a una empresa empleadora.

- **id** (PK, FK): UUID del usuario (`users.id`). CASCADE al eliminar.
- **first_name** / **last_name**: Nombre completo.
- **email**: Correo (único).
- **phone**: Teléfono.
- **company_id** (FK): Empresa (`employers.id`).
- **created_at** / **updated_at**: Timestamps.

```sql
CREATE TABLE recruiters (
    id CHAR(36) PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    company_id CHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES employers(id)
);

CREATE INDEX idx_recruiter_company ON recruiters(company_id);
```

---

## V15 — Terms (Documentos y Aceptaciones)

### **13. Tabla `terms_documents`**

Catálogo de documentos legales publicados (Términos de Servicio, Política de Privacidad, etc.).

- **id** (PK): UUID del documento.
- **terms_type**: Tipo de documento (`TERMS_OF_SERVICE`, `PRIVACY_POLICY`, etc.).
- **version**: Versión del documento (ej. `1`, `2`).
- **content**: Contenido completo del documento.
- **published_at**: Fecha de publicación.
- UNIQUE constraint en `(terms_type, version)`.

```sql
CREATE TABLE terms_documents (
    id           CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL PRIMARY KEY,
    terms_type   VARCHAR(30) NOT NULL,
    version      VARCHAR(10) NOT NULL,
    content      TEXT        NOT NULL,
    published_at TIMESTAMP   NOT NULL,
    UNIQUE KEY uk_terms_type_version (terms_type, version)
);
```

### **14. Tabla `user_terms_acceptances`**

Registro de auditoría de aceptaciones de documentos legales por usuario.

- **id** (PK): UUID de la aceptación.
- **user_id** (FK): Usuario que aceptó (`users.id`).
- **terms_document_id** (FK): Documento aceptado (`terms_documents.id`).
- **accepted_at**: Timestamp de la aceptación.

```sql
CREATE TABLE user_terms_acceptances (
    id                CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL PRIMARY KEY,
    user_id           CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    terms_document_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    accepted_at       TIMESTAMP NOT NULL,
    CONSTRAINT fk_uta_user FOREIGN KEY (user_id)           REFERENCES users(id),
    CONSTRAINT fk_uta_doc  FOREIGN KEY (terms_document_id) REFERENCES terms_documents(id)
);

CREATE INDEX idx_uta_user_id ON user_terms_acceptances(user_id);
```

> **Nota de collation:** Las columnas `CHAR(36)` declaran explícitamente `utf8mb4_0900_ai_ci` para
> garantizar compatibilidad con las tablas existentes independientemente del collation por defecto
> del servidor MySQL (que puede variar entre entornos local y Docker).

---

## V16 — Tipo de empresa

### Tabla `employers` (ALTER)

Agrega `employer_type` para representar la naturaleza real de la empresa y ser transparente frente a los candidatos.

- **employer_type**: `HR_AGENCY`, `SOFTWARE_HOUSE`, `CONSULTING_FIRM`, `STARTUP`, `ENTERPRISE`, `OTHER`.

```sql
ALTER TABLE employers
    ADD COLUMN employer_type VARCHAR(30) NULL;
-- Valores: HR_AGENCY | SOFTWARE_HOUSE | CONSULTING_FIRM | STARTUP | ENTERPRISE | OTHER
```

---

## V17 — Modelo de publicación dual en `jobs`

Separa **quién publica** la oferta del **empleador que contrata**, permitiendo recruiters independientes, freelancers y empresas en el mismo modelo.

### Tabla `jobs` (ALTER)

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `created_by_user_id` | `CHAR(36)` FK → `users.id` | Usuario que creó/publicó la oferta. Obligatorio. |
| `posted_on_behalf_of_employer_id` | `CHAR(36)` FK → `employers.id` (nullable) | Empresa a quien representa el recruiter. Null si es oferta independiente. |
| `job_type` | `VARCHAR(20)` | `TRADITIONAL`, `FREELANCE`, `PROJECT`, `INFORMAL`. |
| `remote_allowed` | `BOOLEAN` | Si acepta trabajo remoto. |
| `expires_at` | `TIMESTAMP` (nullable) | Fecha de expiración automática. |
| `featured` | `BOOLEAN` | Si la oferta está destacada/promocionada. |
| `featured_until` | `TIMESTAMP` (nullable) | Fin del periodo de destaque. |

```sql
ALTER TABLE jobs
    ADD COLUMN created_by_user_id            CHAR(36) NULL,
    ADD COLUMN posted_on_behalf_of_employer_id CHAR(36) NULL,
    ADD COLUMN job_type                      VARCHAR(20) NOT NULL DEFAULT 'TRADITIONAL',
    ADD COLUMN remote_allowed                BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN expires_at                    TIMESTAMP NULL,
    ADD COLUMN featured                      BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN featured_until                TIMESTAMP NULL,
    ADD CONSTRAINT fk_job_created_by   FOREIGN KEY (created_by_user_id)
        REFERENCES users(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_job_on_behalf_of FOREIGN KEY (posted_on_behalf_of_employer_id)
        REFERENCES employers(id) ON DELETE SET NULL;

CREATE INDEX idx_job_created_by ON jobs(created_by_user_id);
CREATE INDEX idx_job_type       ON jobs(job_type);
CREATE INDEX idx_job_featured   ON jobs(featured);
```

> **Relación con `employer_id` (V11):** `employer_id` original se conserva por compatibilidad y puede
> representar el empleador que contrata (quién paga el salario). `posted_on_behalf_of_employer_id`
> representa la empresa para la que trabaja el recruiter que publica. Pueden coincidir o diferir.

---

## V18 — Asociaciones recruiter ↔ empresas (many-to-many)

Un recruiter puede trabajar para múltiples empleadores simultáneamente o ser independiente (freelance recruiter). La tabla `recruiters.company_id` existente queda como empresa principal; esta tabla es el historial de asociaciones.

### **Tabla `recruiter_employer_associations`**

- **id** (PK): UUID.
- **recruiter_user_id** (FK): Usuario con rol RECRUITER (`users.id`).
- **employer_id** (FK): Empresa asociada (`employers.id`).
- **role_in_company**: Descripción del rol (`INTERNAL`, `EXTERNAL`, `FREELANCE`).
- **active**: Si la asociación está vigente.
- **started_at** / **ended_at**: Vigencia.

```sql
CREATE TABLE recruiter_employer_associations (
    id               CHAR(36) PRIMARY KEY,
    recruiter_user_id CHAR(36) NOT NULL,
    employer_id      CHAR(36) NOT NULL,
    role_in_company  VARCHAR(20) NOT NULL DEFAULT 'INTERNAL',
    active           BOOLEAN NOT NULL DEFAULT TRUE,
    started_at       TIMESTAMP NOT NULL,
    ended_at         TIMESTAMP NULL,
    UNIQUE KEY uk_recruiter_employer (recruiter_user_id, employer_id),
    FOREIGN KEY (recruiter_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (employer_id)       REFERENCES employers(id) ON DELETE CASCADE
);

CREATE INDEX idx_rea_recruiter ON recruiter_employer_associations(recruiter_user_id);
CREATE INDEX idx_rea_employer  ON recruiter_employer_associations(employer_id);
```

---

## V19 — Extensión de perfil candidato

Agrega campos para matching y soporte de trabajos freelance/proyectos.

### Tabla `candidates` (ALTER)

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `years_of_experience` | `SMALLINT` (nullable) | Años de experiencia total. |
| `desired_salary_min` | `DECIMAL(15,2)` (nullable) | Expectativa salarial mínima. |
| `desired_salary_max` | `DECIMAL(15,2)` (nullable) | Expectativa salarial máxima. |
| `desired_employment_type` | `VARCHAR(20)` (nullable) | Tipo de empleo buscado. |
| `open_to_remote` | `BOOLEAN` | Si el candidato acepta remoto. |
| `available_for_freelance` | `BOOLEAN` | Si el candidato acepta proyectos freelance. |

```sql
ALTER TABLE candidates
    ADD COLUMN years_of_experience      SMALLINT NULL,
    ADD COLUMN desired_salary_min       DECIMAL(15,2) NULL,
    ADD COLUMN desired_salary_max       DECIMAL(15,2) NULL,
    ADD COLUMN desired_employment_type  VARCHAR(20) NULL,
    ADD COLUMN open_to_remote           BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN available_for_freelance  BOOLEAN NOT NULL DEFAULT FALSE;
```

---

## V20 — Perfil de freelancer

Perfil público independiente para usuarios con rol FREELANCER. Complementa (no reemplaza) el perfil de candidato.

### **Tabla `freelancer_profiles`**

- **id** (PK, FK): UUID del usuario (`users.id`). CASCADE al eliminar.
- **headline**: Título profesional corto (ej. "Senior React Developer").
- **hourly_rate_min** / **hourly_rate_max**: Rango de tarifa por hora (`DECIMAL(10,2)`).
- **currency**: Moneda de la tarifa (ej. `USD`).
- **availability**: Disponibilidad (`FULL_TIME`, `PART_TIME`, `WEEKENDS`, `ON_DEMAND`).
- **portfolio_url**: Enlace a portfolio.
- **bio**: Descripción extendida del freelancer.
- **created_at** / **updated_at**: Timestamps.

```sql
CREATE TABLE freelancer_profiles (
    id               CHAR(36) PRIMARY KEY,
    headline         VARCHAR(150) NOT NULL,
    hourly_rate_min  DECIMAL(10,2) NULL,
    hourly_rate_max  DECIMAL(10,2) NULL,
    currency         VARCHAR(10) NULL,
    availability     VARCHAR(20) NOT NULL DEFAULT 'ON_DEMAND',
    portfolio_url    VARCHAR(500) NULL,
    bio              TEXT NULL,
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);
```

---

## V21 — Modalidad de trabajo en `jobs`

Reemplaza el booleano `remote_allowed` (V17) con un campo de enum explícito que modela con precisión si una oferta es presencial, remota o híbrida.

### Tabla `jobs` (ALTER)

| Columna | Tipo | Valores | Descripción |
|---------|------|---------|-------------|
| `work_modality` | `VARCHAR(20)` | `REMOTE`, `HYBRID`, `ON_SITE` | Modalidad de trabajo de la oferta. Default `ON_SITE`. |

```sql
ALTER TABLE jobs
    ADD COLUMN work_modality VARCHAR(20) NOT NULL DEFAULT 'ON_SITE';

CREATE INDEX idx_job_work_modality ON jobs(work_modality);
```

> **Nota:** `remote_allowed` (V17) queda en la BD por compatibilidad retroactiva pero la fuente de verdad
> para la modalidad es `work_modality`. Los valores válidos son gestionados por el enum `WorkModality`
> en el dominio (`com.ITJobsBackend.jobs.domain.valueobjects.WorkModality`).

