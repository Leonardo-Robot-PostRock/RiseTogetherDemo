# Matching — Rise Together

> **Estado**: Planificado — Sin código

## Descripción

Fair Match explicable entre candidatos y ofertas. Es la **ventaja competitiva** del producto.

## Dominio (Planeado)

| Aggregate | Descripción |
|-----------|-------------|
| `SkillCatalog` | Catálogo maestro de skills normalizados |
| `Skill` | Skill individual con categoría, sinónimos, relaciones |
| `MatchResult` | Resultado del matching con score y explicación |

## Value Objects (Planeado)

| VO | Descripción |
|----|-------------|
| `SkillId` | UUID del skill |
| `SkillCategory` | LANGUAGE, FRAMEWORK, DATABASE, TOOL, SOFT_SKILL |
| `RelationshipType` | EQUIVALENT, ADJACENT, PARENT, CHILD |
| `MatchCategory` | EXACT, TRANSFERABLE, PARTIAL, EMERGENT |

## Algoritmo

| Tipo | Descripción |
|------|-------------|
| **Exact Match** | Skill requerido = skill del candidato (mismo nivel o superior) |
| **Transferable** | Skill relacionado con peso < 1.0 (ej. Java → Kotlin) |
| **Partial** | Skill con nivel inferior al requerido |
| **Emergent** | Skill adyacente que el candidato podría adquirir |

## DB (Planeado)

- **V11** (planeado): `skills`, `skill_synonyms`, `skill_relationships`
- **V12** (planeado): `job_skills.skill_id`, `candidate_skills.skill_id`, `proficiency_level`

## Pending

- [ ] Catálogo maestro de skills (CRUD + sinónimos + relaciones)
- [ ] Algoritmo de scoring (score 0-100)
- [ ] Generación de explicación JSON
- [ ] Clasificación de match type
- [ ] Reverse matching (sugerir ofertas al candidato)