# Profiles — ITJobs Backend

> **Estado**: Pendiente — DB creada (V3, V19, V20), sin implementación

## Contenido

- `diagrams/` — PlantUML diagrams

## Dominio (Planeado)

| Aggregate | Descripción |
|-----------|-------------|
| `CandidateAggregate` | Perfil candidato con skills y experiencia |
| `EmployerAggregate` | Perfil empresa con logo, descripción, tipo |
| `RecruiterAggregate` | Perfil reclutador con asociaciones |

## Value Objects (Planeados)

| VO | Descripción |
|----|-------------|
| `CandidateId` | UUID del candidato |
| `EmployerId` | UUID del empleador |
| `ProficiencyLevel` | BEGINNER, INTERMEDIATE, ADVANCED, EXPERT |
| `RequirementLevel` | REQUIRED, DESIRED, OPTIONAL |
| `EmployerType` | HR_AGENCY, SOFTWARE_HOUSE, CONSULTING_FIRM, STARTUP, ENTERPRISE, OTHER |
| `CompanySize` | SMALL, MEDIUM, LARGE |

## DB (Migraciones aplicadas)

- **V3**: `employers`, `candidates`, `candidate_skills`
- **V6**: `recruiters`
- **V12**: `employers.company_size`
- **V13**: `employers.logo_url`, `employers.description`
- **V16**: `employers.employer_type`
- **V18**: `recruiter_employer_associations`
- **V19**: `candidates` extendida con experiencia, salario deseado, remoto, freelance
- **V20**: `freelancer_profiles`

## Pending

- [ ] CRUD CandidateAggregate
- [ ] CRUD EmployerAggregate
- [ ] CRUD RecruiterAggregate
- [ ] Gestión de skills (catálogo vs string libre)