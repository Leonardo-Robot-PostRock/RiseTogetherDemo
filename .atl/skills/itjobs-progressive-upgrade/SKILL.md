# ITJobs Backend — Progressive JDK & Spring Boot Upgrade

> Guía para actualizar JDK y Spring Boot de forma incremental sin romper el proyecto.

## Estado Actual (Abr 2026)

```
Spring Boot: 4.0.5 ✅
JDK: 21 ✅
```

## Enfoque: Actualización Incremental

NUNCA hacer upgrade mayor (3.x → 4.x) y JDK mayor simultáneamente.### Estrategia Recomendada

| Paso | Phase | Spring Boot | JDK | Razón |
|-----|-------|-------------|-----|-------|
| 1   | Actual | 3.5.13 | 17 | Baseline |
| 2   | Safe   | 3.5.14 | 17 | Patch only |
| 3   | Safe   | 3.5.14 | 21 | LTS estable |
| 4   | Safe   | 3.5.14 | 25 | Latest stable |
| 5   | Major | 4.0.x | 25 | Break (when needed) |

## Phase 1: Actualizar Spring Boot (mismo JDK)

Solo actualizar el patch version — bajo riesgo:

```bash
# En pom.xml, actualizar:
<version>3.5.14</version>  <!-- de 3.5.13 -->
```

```bash
# Verificar
./mvnw clean compile
./mvnw test
```

**Expected**: ~0 breaking changes en patch release.

## Phase 2: Upgrade JDK (mismo Spring Boot)

Atualizar JDK manteniendo Spring Boot:

```xml
<!-- En pom.xml -->
<maven.compiler.release>21</maven.compiler.release>
```

```bash
# Verificar compilación
./mvnw clean compile
./mvnw test
```

### Si falla: Common Issues

| Issue | Solución |
|-------|----------|
| JAXB removed in JDK 11+ | Añadir dependencia `jakarta.xml.bind` |
| `IllegalArgumentException: Java XX not supported` | Update ByteBuddy (ver debajo) |

### ByteBuddy Override

Si hay error de ByteBuddy:

```xml
<!-- En pom.xml properties -->
<bytebuddy.version>1.17.0</bytebuddy.version>
```

O en dependencies:

```xml
<dependency>
    <groupId>net.bytebuddy</groupId>
    <artifactId>byte-buddy</artifactId>
    <version>1.17.0</version>
</dependency>
```

## Phase 3: Upgrade a JDK 25

```xml
<maven.compiler.release>25</maven.compiler.release>
```

**Nota**: JDK 25 soportado desde Spring Boot 3.5.5+.

Ver: [spring-projects/spring-boot#46907](https://github.com/spring-projects/spring-boot/issues/46907)

## Phase 4: Upgrade a Spring Boot 4.x (When Needed)

**⚠️ BREAKING CHANGES**: Spring Boot 4.x requiere:

- Java 17+ (✓ ya cubierto)
- Jakarta EE 9+ (`jakarta.*` en vez de `javax.*`)
- Spring Framework 6+
- Actualización de dependencias mayores

### Pre-Upgrade Checklist

1. ✅ Tests pasando en Spring Boot 3.5.x + JDK 25
2. ✅ `grep -r "javax\." src/` — replace `javax.servlet` → `jakarta.servlet`
3. ✅ `grep -r "javax\." src/` — replace `javax.persistence` → `jakarta.persistence`
4. ✅ Revisar breaking changes de librerías (JJWT, etc.)

### Execution

```xml
<!-- En pom.xml -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.6</version>  <!-- verificar latest -->
</parent>
```

```bash
./mvnw clean compile
# Si falla: fix imports javax → jakarta
./mvnw test
```

## Commands de Verificación

```bash
# Siempre ejecutar después de cada phase
./mvnw clean compile

# Si compila, tests
./mvnw test

# Si todo pasa, verificar contexto Spring
./mvnw spring-boot:run
```

## Spring Boot 4.0.x Notes

Según system requirements actual:

- Spring Boot 4.0.6+ requiere Java 17+
- Compatible con Java 26
- Spring Framework 7.0.7+ requerido
- Tomcat 11.x, Jetty 12.x

## Errores a Evitar

- ❌ No fazer upgrade de JDK y Spring Boot simultáneamente
- ❌ No saltar de 3.5.x directamente a 4.0.x sin tests
- ❌ No asumir backward compatibility

## Referencia

- [Spring Boot System Requirements](https://docs.spring.io/spring-boot/system-requirements.html)
- [Spring Boot 3.5.14 Release](https://spring.io/blog/2026/04/23/spring-boot-3-5-14-available-now)
- [GitHub Issue #46907](https://github.com/spring-projects/spring-boot/issues/46907) — JDK 25 support