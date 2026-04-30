# Analytics — Rise Together

> **Estado**: Planificado — Sin código

## Descripción

Read-model para métricas y dashboards. Consume eventos de otros bounded contexts.

## Dominio (Planeado)

| Aggregate | Descripción |
|-----------|-------------|
| `JobMetricsAggregate` | Métricas por oferta |
| `EmployerDashboardAggregate` | Dashboard del empleador |

## Value Objects (Planeado)

| VO | Descripción |
|----|-------------|
| `JobViewEvent` | Evento: oferta vista por usuario |
| `ConversionMetrics` | Vistas → Postulaciones → Pipeline |

## DB (Planeado)

- `job_views` — Tracking de vistas de ofertas
- Views materializadas sobre datos existentes

## Dashboards (Planeado)

### Employer Dashboard
- Ofertas publicadas vs activas
- Postulaciones recibidas (total y por oferta)
- Tasa de conversión (vistas → postulaciones)
- Tiempo promedio en cada etapa del pipeline

### Job Analytics
- Vistas por oferta
- Postulaciones por oferta
- Tasa de conversión
- Distribución por etapa del pipeline

## Pending

- [ ] Registrar vista de oferta
- [ ] Dashboard overview del empleador
- [ ] Métricas por oferta
- [ ] Exportación CSV (limitado por plan)
- [ ] Score de calidad/inclusividad de ofertas