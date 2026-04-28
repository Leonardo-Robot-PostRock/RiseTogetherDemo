# Jobs — ITJobs Backend

> **Estado**: Implementado (parcialmente)

## Contenido

- `diagrams/` — PlantUML diagrams

## Dominio

| Aggregate | Descripción |
|-----------|-------------|
| `JobAggregate` | Oferta de empleo: create, close, deactivate, addSkill |

## Value Objects

| VO | Descripción |
|----|-------------|
| `JobId` | UUID de la oferta |
| `JobStatus` | OPEN, CLOSED, DRAFT, EXPIRED, INACTIVE |
| `EmploymentType` | FULL_TIME, PART_TIME, CONTRACT, FREELANCE, INTERNSHIP |
| `WorkModality` | REMOTE, HYBRID, ON_SITE |
| `Salary` | Rango salarial (min/max DECIMAL) |

## Specifications (Filtering)

| Spec | Filter |
|------|--------|
| `TitleContainsSpecification` | title LIKE %keyword% |
| `CompanySpecification` | exact company match |
| `LocationSpecification` | exact location match |
| `JobStatusSpecification` | exact status match |

## Diagramas

| Diagrama | Descripción |
|----------|-------------|
| `diagrams/class-diagram-jobs.puml` | Clase: JobAggregate, VOs, specs, ports |
| `diagrams/seq-apply.puml` | Sequence: job application flow |
| `diagrams/act-job-management.puml` | Activity: job management |
| `diagrams/state-job.puml` | State: job lifecycle |
| `diagrams/state-application.puml` | State: application lifecycle |

## REST API

| Method | Path | Use Case | Auth |
|--------|------|----------|------|
| POST | /api/v1/jobs | CreateJobUseCase | No |
| GET | /api/v1/jobs | SearchJobsUseCase | No |