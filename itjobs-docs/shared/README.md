# Shared — ITJobs Backend

Kernel compartido entre todos los bounded contexts.

## Contenido

- [Arquitectura](architecture.md) — Profiles, entornos, Docker, CQRS
- [Onboarding Hexagonal](hexagonal-onboarding.md) — Guía de incorporación
- [Code Style](code-style.md) — Google Java Format, imports, convenciones
- [Testing](testing.md) — Estrategia de tests, BDD Mockito
- [Database](database.md) — Migraciones Flyway, esquema completo

## Diagramas

| Diagrama | Descripción |
|----------|-------------|
| `diagrams/class-diagram-shared.puml` | Value objects, DomainEvent, excepciones compartidas |
| `diagrams/class-diagram.puml` | ⚠️ Deprecated — ver diagramas por context |
| `diagrams/context-map.puml` | Relaciones entre bounded contexts |
| `diagrams/er-diagram.puml` | ER completo de la base de datos |
| `diagrams/use-cases.puml` | Actores y use cases across all contexts |