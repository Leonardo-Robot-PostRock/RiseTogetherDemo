# Moderation — ITJobs Backend

> **Estado**: Planificado — Sin código

## Descripción

Sistema de moderación de contenido con reportes, decisiones, apelaciones y auditoría.

## Dominio (Planeado)

| Aggregate | Descripción |
|-----------|-------------|
| `ReportAggregate` | Reporte de contenido inapropiado |
| `ModerationDecisionAggregate` | Decisión del moderador |
| `AppealAggregate` | Apelación de una decisión |

## Value Objects (Planeado)

| VO | Descripción |
|----|-------------|
| `ReportReason` | SPAM, MISLEADING, DISCRIMINATORY, IMMORAL, DUPLICATE, OTHER |
| `ReportStatus` | PENDING, RESOLVED, DISMISSED |
| `ModerationDecision` | APPROVED, REJECTED, ESCALATED |
| `ModerationAction` | CONTENT_REMOVED, USER_WARNED, USER_BANNED, NONE |
| `AppealStatus` | PENDING, ACCEPTED, REJECTED |

## DB (Planeado)

- `reports` — Reportes de contenido
- `moderation_decisions` — Decisiones del moderador
- `moderation_appeals` — Apelaciones
- `job_feedback` — Feedback estructurado (1-5 en dims)
- `users.banned_until`, `users.ban_reason`

## Pending

- [ ] Reportar oferta/contenido
- [ ] Revisar reportes pendientes
- [ ] Tomar decisión (aprobar/rechazar/escalar)
- [ ] Banear usuario temporal/permanente
- [ ] Apelar decisión
- [ ] Feedback estructurado de ofertas