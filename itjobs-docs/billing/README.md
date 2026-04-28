# Billing — ITJobs Backend

> **Estado**: Planificado — Sin código

## Descripción

Monetización B2B: planes de suscripción, pagos, límites por feature.

## Dominio (Planeado)

| Aggregate | Descripción |
|-----------|-------------|
| `SubscriptionPlanAggregate` | Definición de plan (FREE, STARTER, PROFESSIONAL, ENTERPRISE) |
| `SubscriptionAggregate` | Suscripción activa de un empleador |
| `PaymentAggregate` | Pago asociado a una suscripción |

## Value Objects (Planeado)

| VO | Descripción |
|----|-------------|
| `PlanName` | FREE, STARTER, PROFESSIONAL, ENTERPRISE |
| `SubscriptionStatus` | ACTIVE, CANCELLED, EXPIRED, PAST_DUE |
| `BillingCycle` | MONTHLY, YEARLY |
| `PaymentStatus` | PENDING, COMPLETED, FAILED, REFUNDED |

## Límites por Plan (Planeado)

| Feature | FREE | STARTER | PROFESSIONAL | ENTERPRISE |
|---------|------|---------|--------------|------------|
| Ofertas activas | 3 | 10 | 50 | unlimited |
| Ofertas destacadas | 0 | 2 | 10 | unlimited |
| Reclutadores | 1 | 3 | 10 | unlimited |
| Exportar CSV | No | No | Yes | Yes |
| Ver match details | No | No | No | Yes |
| Notas en postulaciones | Yes | Yes | Yes | Yes |

## DB (Planeado)

- `subscription_plans` — Catálogo de planes
- `subscriptions` — Suscripciones activas
- `payments` — Registro de pagos

## Pending

- [ ] CRUD de planes (admin)
- [ ] Suscribirse a un plan
- [ ] Validar límites al publicar oferta
- [ ] Registrar pago
- [ ] Desbloquear features según plan
- [ ] Cancelar suscripción