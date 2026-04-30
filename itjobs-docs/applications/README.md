# Applications — Rise Together

> **Estado**: Pendiente — DB creada (V4), sin implementación

## Contenido

- `diagrams/` — PlantUML diagrams

## Dominio (Planeado)

| Aggregate | Descripción |
|-----------|-------------|
| `ApplicationAggregate` | Postulación con pipeline extendido |

## Value Objects (Planeados)

| VO | Descripción |
|----|-------------|
| `ApplicationId` | UUID de la postulación |
| `ApplicationStatus` | PENDING, SCREENING, INTERVIEW, OFFER, ACCEPTED, REJECTED, WITHDRAWN |
| `MatchScore` | Score 0-100 + explicación JSON |

## DB (Migraciones aplicadas)

- **V4**: `applications`, `saved_jobs`
- **V15**: `match_score`, `match_explanation`, `match_category` en `applications` (planeado)
- **V16**: `application_notes`, `application_status_history` (planeado)

## Pending

- [ ] Postularse a oferta (CreateApplicationUseCase)
- [ ] Ver postulaciones por candidato
- [ ] Ver postulaciones por oferta (recruiter/employer)
- [ ] Mover en pipeline
- [ ] Agregar notas privadas
- [ ] Historial de estados
- [ ] Guardar favoritos

## REST API (Planeado)

| Method | Path | Use Case |
|--------|------|----------|
| POST | /api/v1/candidate/applications | CreateApplicationUseCase |
| GET | /api/v1/candidate/applications | ListMyApplicationsUseCase |
| GET | /api/v1/dashboard/jobs/{id}/applications | ListJobApplicationsUseCase |
| PUT | /api/v1/dashboard/jobs/{id}/applications/{id}/stage | MoveStageUseCase |
| POST | /api/v1/candidate/saved-jobs | SaveJobUseCase |