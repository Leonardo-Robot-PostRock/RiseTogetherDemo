## **Documentación de la Base de Datos para el Sistema de Búsqueda de Empleo**

> **Nota:** Todos los IDs usan UUID (`CHAR(36)`). El esquema está versionado con Flyway (V1–V9).

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
| V10        | Agregar `verification_token` y expiración a `users`   

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
- **status**: Estado (`OPEN`, `CLOSED`, `DRAFT`, `EXPIRED`, `INACTIVE`).
- **created_at**: Fecha de creación.
- **updated_at**: Fecha de última actualización.

```sql
CREATE TABLE jobs (
    id CHAR(36) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    company VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,  -- obligatorio desde ahora
    salary_min DOUBLE,                -- V7: ALTER to DECIMAL(15,2)
    salary_max DOUBLE,                -- V7: ALTER to DECIMAL(15,2)
    currency VARCHAR(10),
    employment_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL, -- OPEN, CLOSED, DRAFT, EXPIRED, INACTIVE
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_job_title ON jobs(title);
CREATE INDEX idx_job_company ON jobs(company);
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
- **created_at** / **updated_at**: Timestamps.

> Futuro: `jobs.company` podría reemplazarse por `jobs.employer_id` FK a esta tabla.

```sql
CREATE TABLE employers (
    id CHAR(36) PRIMARY KEY,
    company_name VARCHAR(100) NOT NULL,
    industry VARCHAR(50),
    website VARCHAR(255),
    location VARCHAR(100),
    contact_person VARCHAR(100),
    contact_email VARCHAR(255),
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