# ITJobs Backend — Documentation

Documentación organizada por **bounded context** siguiendo Arquitectura Hexagonal.

---

## Bounded Contexts

| Context | Estado | Descripción |
|---------|--------|-------------|
| [authentication](auth/) | Implementado | Registro, login, JWT, Google OAuth, verificación email |
| [jobs](jobs/) | Implementado | Ofertas, búsqueda, habilidades |
| [profiles](profiles/) | Pendiente (DB lista) | Candidatos, empleadores, reclutadores |
| [applications](applications/) | Pendiente (DB lista) | Postulaciones, pipeline, favoritos |
| [matching](matching/) | Planificado | Catálogo skills, algoritmo match |
| [moderation](moderation/) | Planificado | Reportes, decisiones, apelaciones |
| [billing](billing/) | Planificado | Planes, suscripciones, pagos |
| [analytics](analytics/) | Planificado | Métricas, dashboards, exportación |
| [administration](administration/) | Planificado | Gestión admin, auditoría |
| [shared](shared/) | Implementado | Kernel compartido: value objects, arquitectura, testing |

---

## Docs Transversales

- [Requirements v2](requirements/requirements-v2.md) — Especificación completa
- [Requirements v1](requirements/requirements-v1.md) — Versión original

---

## Diagrama de Contexto

```
Authentication ──▶ Profiles (crea perfil)
             ──▶ Applications (postulaciones)

Jobs ──▶ Analytics (vistas, métricas)
     ──▶ Matching (catálogo skills)
     ──▶ Billing (validación límites)

Applications ──▶ Matching (calcula score)
            ──▶ Analytics (pipeline)

Matching ◀── Profiles (skills candidato)
        ◀── Jobs (skills oferta)

Moderation ──▶ Jobs (desactiva reportados)
           ──▶ Analytics (feedback)

Billing ──▶ Jobs (feature limits)
```

Para diagramas PlantUML, ver la carpeta `diagrams/` dentro de cada context.