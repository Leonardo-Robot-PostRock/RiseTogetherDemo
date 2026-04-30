# Administration — ITJobs Backend

> **Estado**: Planificado — Sin código

## Descripción

Gestión administrativa: usuarios, roles, categorías, help center, onboarding, auditoría.

## Dominio (Planeado)

| Aggregate | Descripción |
|-----------|-------------|
| `SkillManagementAggregate` | CRUD del catálogo de skills |
| `CategoryManagementAggregate` | CRUD de categorías |
| `HelpCenterAggregate` | Artículos y categorías del help center |
| `OnboardingFlowAggregate` | Flujos y pasos de onboarding |
| `AuditLogAggregate` | Registro de auditoría |

## Value Objects (Planeado)

| VO | Descripción |
|----|-------------|
| `OnboardingStep` | Paso individual del tutorial |
| `StepType` | TOOLTIP, MODAL, HIGHLIGHT, VIDEO |
| `OnboardingProgress` | Progreso del usuario en un flujo |
| `AuditEntry` | Entrada del log de auditoría |

## DB (Planeado)

- `skills` (V11 planeado) — Catálogo maestro
- `skill_synonyms` (V11 planeado) — Sinónimos
- `skill_relationships` (V11 planeado) — Relaciones
- `audit_log` — Log de auditoría
- `onboarding_flows`, `onboarding_steps`, `user_onboarding_progress`
- `help_categories`, `help_articles`

## REST API (Planeado)

| Path | Descripción |
|------|-------------|
| `/api/v1/admin/users` | Gestionar usuarios |
| `/api/v1/admin/skills` | CRUD skills + sinónimos + relaciones |
| `/api/v1/admin/categories` | CRUD categorías |
| `/api/v1/admin/subscription-plans` | CRUD planes |
| `/api/v1/admin/onboarding/flows` | CRUD flujos de onboarding |
| `/api/v1/admin/help/articles` | CRUD artículos del help center |
| `/api/v1/admin/audit-log` | Ver log de auditoría |

## Pending

- [ ] Gestión de usuarios (ban, roles)
- [ ] CRUD catálogo de skills
- [ ] CRUD categorías
- [ ] CRUD planes de suscripción
- [ ] Help center
- [ ] Onboarding guiado
- [ ] Log de auditoría