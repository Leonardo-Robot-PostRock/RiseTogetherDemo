# TODO — ITJobs Backend

## Semana actual / próxima

### Alta prioridad (bloquea funcionalidad o seguridad)

- [ ] SECURITY: Proteger `POST /api/v1/jobs` con JWT (actualmente cualquiera puede crear jobs)

### Media prioridad (arquitectura, tests)

- [ ] Eliminar warning `UserDetailsServiceAutoConfiguration` (Spring crea usuario in-memory innecesario)

### Baja prioridad (refactors, futuros)

- [ ] PUNTO 9: Eliminar `fromDomain()` de entidades JPA (usar solo `UserMapper`, `JobMapper`)
- [ ] PUNTO 10: Fix pom.xml — usar `maven.compiler.release` en vez de propiedades inventadas
- [ ] Crear entorno staging/QA con profile `staging`
- [ ] Implementar bounded context `Applications` (postulaciones, favoritos)
- [ ] Implementar bounded context `Profiles` (candidatos, empleadores, reclutadores)
- [ ] Implementar bounded context `Administration` (back-office, categorías, roles)
- [ ] Implementar refresh token endpoint
- [ ] Implementar verificación de email
- [ ] Asignar skills a oferta (endpoint)
- [ ] Asignar categorías a oferta (endpoint)
- [ ] Agregar i18n para respuestas en en y es al cliente
- [ ] Agregar más campos para entidad User (img, etc.)
- [ ] Agregar creación de cuenta con Apple o Google

---

## Completados

- [x] PUNTO 1: Docker Compose + env vars (commits b538cd6, 8a0dec9, d6748ea)
- [x] PUNTO 2: JWT Authentication Filter (commit b72acab)
- [x] PUNTO 3: Salary double → BigDecimal (commit 13340d9)
- [x] PUNTO 4: CreateJobRequest DTO en JobController (commit e58d4be)
- [x] PUNTO 5: JobAggregate state validation (commit 54e0187)
- [x] PUNTO 6: LoginUseCase password verification (commit 4fdf0dc)
- [x] PUNTO 7: Inbound ports para Jobs (CreateJobPort, SearchJobsPort)
- [x] PUNTO 8: Tests unitarios para JobAggregate, LoginUseCase, Salary
- [x] Profiles Spring Boot: dev / prod (commit 7198252)
- [x] Fix Hibernate UUID → VARCHAR (commit d4a49af)
- [x] Diagramas UML en `itjobs-docs/diagrams/`
- [x] Requisitos funcionales y no funcionales en `itjobs-docs/requirements.md`
- [x] Documentación de arquitectura en `itjobs-docs/architecture.md`
