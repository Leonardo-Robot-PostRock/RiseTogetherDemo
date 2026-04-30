# Authentication — Agenda & Decisiones de Diseño

> Documenta decisiones clave, trade-offs y roadmap del bounded context Authentication.

---

## Decisiones de Diseño

### 1. CQRS: Split Read/Write en User

**Decisión**: Separar `LoadUserPort` (command/write) de `QueryUserPort` (query/read) para User.

**Motivación**: `UserAggregate` es costoso de instanciar (invariantes, lista de eventos). Usarlo solo para leer un email+password en Login es innecesario.

**Trade-off**: Más interfaces, más adaptadores. Beneficio: read model plano, optimizable independientemente.

**Referencia**: [`shared/architecture.md`](../shared/architecture.md) — sección CQRS

---

### 2. Terms Acceptance fuera del Aggregate

**Decisión**: `TermsAcceptance` es una entity separada, no un campo en `UserAggregate`.

**Motivación**: Registro de auditoría immutable. Un usuario puede aceptar múltiples versiones de ToS a lo largo del tiempo.

**Trade-off**: Más tablas, más código. Beneficio: trazabilidad completa.

---

### 3. Polimorfismo por Configuración en EmailSender

**Decisión**: Dos implementaciones de `EmailSenderPort` con condiciones `@Conditional*`.

| Adapter | Condición |
|---------|-----------|
| `SmtpEmailSenderAdapter` | `@ConditionalOnProperty(spring.mail.host)` |
| `NoOpEmailSenderAdapter` | `@ConditionalOnMissingBean` |

**Motivación**: Tests y dev local sin SMTP no deben romper el contexto de Spring.

**Trade-off**: Posible confusión si ambos adapters se registran (no ocurre si se usa la condición correcta).

---

### 4. JWT Stateless

**Decisión**: Access token 15 min + Refresh token 7 días. No usar sesiones server-side.

**Motivación**: Escalabilidad horizontal (no hay estado en el servidor).

**Trade-off**: Refresh token debe almacenarse client-side (localStorage/cookie httpOnly).

---

## Trade-offs Resueltos

| Tema | Decisión | Alternativa Considerada |
|------|----------|------------------------|
| Token storage | JWT stateless + refresh token | Sessiones server-side (rechazado: no escala) |
| Password reset | Evento `PasswordResetRequestedEvent` | Enviar email directamente en use case (rechazado: acoplamiento) |
| Google OAuth | `GoogleSub` como Value Object | Google email como identificador (rechazado: email puede cambiar) |
| Verification token | UUID en DB con expiración | JWT token (rechazado: no se puede invalidar) |

---

## Pending

- [ ] Endpoint de ForgotPassword en AccountController (implementado pero no conectado)
- [ ] Update email (RF-06 en requirements)
- [ ] Activar/desactivar usuario (RF-04 en requirements)