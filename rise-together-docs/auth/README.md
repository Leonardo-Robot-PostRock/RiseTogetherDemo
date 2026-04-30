# Authentication — Rise Together

> **Estado**: Implementado (parcialmente)

## Contenido

- [agenda.md](agenda.md) — Decisiones de diseño y roadmap
- `diagrams/` — PlantUML diagrams

## Dominio

| Aggregate | Descripción |
|-----------|-------------|
| `UserAggregate` | Usuario: registration, login, Google OAuth, email verification |
| `TermsDocument` | Documentos legales (ToS, Privacy Policy) |
| `TermsAcceptance` | Entity — registro de aceptación por usuario |

## Puertos de Entrada

| Port | Use Case |
|------|----------|
| `RegisterUserPort` | Registro con email/password + aceptación ToS |
| `LoginPort` | Login con credenciales (CQRS: QueryUserPort) |
| `GoogleAuthPort` | Login/registro con Google OAuth |
| `VerifyEmailPort` | Verificar email con token |
| `RefreshTokenPort` | Refrescar access token (CQRS: QueryUserPort) |
| `ChangePasswordPort` | Cambiar password (authenticated) |
| `ForgotPasswordPort` | Solicitar reset (CQRS: QueryUserPort) |
| `ResendVerificationPort` | Reenviar email de verificación |
| `DeleteExpiredUnverifiedUsersPort` | Cleanup batch |

## Diagramas

| Diagrama | Descripción |
|----------|-------------|
| `diagrams/auth-overview.puml` | Componente: capas hexagonales + CQRS + email adapters |
| `diagrams/class-diagram-auth.puml` | Clase: UserAggregate, TermsDocument, VOs, eventos, CQRS |
| `diagrams/usecases/*.puml` | Usecases individually |
| `diagrams/sequences/*.puml` | Sequence diagrams |

## REST API

| Method | Path | Use Case | Auth |
|--------|------|----------|------|
| POST | /api/v1/auth/register | RegisterUserUseCase | No |
| POST | /api/v1/auth/login | LoginUseCase | No |
| POST | /api/v1/auth/login/google | GoogleAuthUseCase | No |
| POST | /api/v1/auth/verify-email | VerifyEmailUseCase | No |
| POST | /api/v1/account/change-password | ChangePasswordUseCase | Yes |