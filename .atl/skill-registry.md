# Skill Registry — ITJobsBackend

**For orchestrator use only.** Sub-agents receive compact rules pre-resolved in their launch prompts. Read individual SKILL.md files only when creating this registry.

## Project Skills

| Trigger | Skill | Path |
|---------|-------|------|
| Testing domain classes (Value Objects, Aggregates) without Spring | itjobs-domain-testing | .atl/skills/itjobs-domain-testing/SKILL.md |
| Testing use cases with Mockito BDD | itjobs-usecase-testing | .atl/skills/itjobs-usecase-testing/SKILL.md |
| CQRS implementation in authentication context | itjobs-cqrs-pattern | .atl/skills/itjobs-cqrs-pattern/SKILL.md |
| Adding new features with hexagonal architecture | itjobs-hexagonal-architecture | .atl/skills/itjobs-hexagonal-architecture/SKILL.md |
| Progressive JDK & Spring Boot upgrade without breaking | itjobs-progressive-upgrade | .atl/skills/itjobs-progressive-upgrade/SKILL.md |

## Compact Rules

### itjobs-progressive-upgrade
- NEVER upgrade JDK and Spring Boot simultaneously — do one at a time
- Phase 1: Spring Boot 3.5.13 → 3.5.14 (patch only, same JDK 17)
- Phase 2: JDK 17 → 21 (LTS estable)
- Phase 3: JDK 21 → 25 (latest stable, supported desde 3.5.5+)
- Phase 4: Spring Boot 4.x when needed — requires Jakarta EE 9+ (`jakarta.*` not `javax.*`)
- Always run `./mvnw clean compile` && `./mvnw test` after each phase

### itjobs-domain-testing
- Test Value Objects and Aggregates with plain JUnit 5 — NO Spring, NO Mockito
- Structure: Given / When / Then
- Value Object factory: test `of()`, validation, equals/hashCode
- Aggregate factory: test `create()`, `reconstitute()`, domain events with `pullDomainEvents()`
- NEVER use @Mock or @InjectMocks in domain tests

### itjobs-usecase-testing
- Use ONLY BDDMockito (`given().willReturn()`, `then().should()`) — never mix with classic Mockito
- Mock @Mock only for OUT ports — NEVER mock aggregates, value objects, or domain services (use @Spy)
- Use @InjectMocks for the subject under test — never instantiate manually
- CQRS rule: use QueryUserPort for read-side, LoadUserPort for write-side
- Always use ArgumentCaptor to verify argument state

### itjobs-cqrs-pattern
- Two output ports: LoadUserPort (write) returns UserAggregate, QueryUserPort (read) returns UserView
- Write-side (mutates): VerifyEmailUseCase, ChangePasswordUseCase, GoogleAuthUseCase → LoadUserPort
- Read-side (no mutation): LoginUseCase, RefreshTokenUseCase, ForgotPasswordUseCase → QueryUserPort
- UserView is a record (immutable, no logic) — direct projection from UserEntity
- NEVER return aggregates from query ports

### itjobs-hexagonal-architecture
- Dependencies point INWARD: Infrastructure → Application → Domain
- NEVER inject JpaRepository in UseCase — inject port interface instead
- Validate in Value Objects (Email.of(), Username.of()), never in Controllers
- Domain has NO jakarta.persistence imports
- Pattern: Port interface → UseCase implements Port → Adapter implements Port

## Project Conventions

| File | Path | Notes |
|------|------|-------|
| AGENTS.md | ./AGENTS.md | Main project config — build/test commands, bounded contexts |
| Testing Guide | ./itjobs-docs/shared/testing.md | Full testing patterns (858 lines) |
| Hexagonal Onboarding | ./itjobs-docs/shared/hexagonal-onboarding.md | Architecture guide (485 lines) |
| Architecture | ./itjobs-docs/shared/architecture.md | Profiles, Docker, CQRS, email adapters |
| Auth Docs | ./itjobs-docs/auth/README.md | Bounded context documentation |
| Jobs Docs | ./itjobs-docs/jobs/README.md | Bounded context documentation |

---

Read the convention files above for project-specific patterns and rules. All paths are extracted — no need to read index files to discover more.