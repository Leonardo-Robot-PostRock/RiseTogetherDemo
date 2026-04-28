# Estilo de Código — ITJobs Backend

> **TL;DR:** Instala el plugin **Google Java Format** en IntelliJ, configúralo en AOSP (4 espacios),
> y usa **⌘⌥L** para formatear. Eso es todo.

---

## Convención

- Formatter: **Google Java Format**, estilo **AOSP**
- Indentación: **4 espacios** (AOSP; el estilo `GOOGLE` usa 2)
- Aplica formato con: **⌘⌥L** (macOS) / **Ctrl+Alt+L** (Windows/Linux)

---

## Setup inicial (solo una vez por máquina)

### Paso 1 — Exportar clases internas de la JRE

El plugin necesita acceso a clases internas del compilador de Java. Sin esto, no arranca.

1. Abre **Help → Edit Custom VM Options...**
2. Añade al final:

```
--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
```

3. **Reinicia IntelliJ completamente.**

### Paso 2 — Instalar el plugin

`Settings > Plugins` → buscar **"Google Java Format"** → instalar → reiniciar.

### Paso 3 — Activar en AOSP

`Settings > Other Settings > Google Java Format`

| Opción                    | Valor                 |
|---------------------------|-----------------------|
| Enable Google Java Format | ✅ activado            |
| Code style                | **AOSP** (4 espacios) |

> ⚠️ El valor por defecto es `Google` (2 espacios). Asegúrate de seleccionar **AOSP**.

### Paso 4 — Verificar

Abre cualquier `.java` y presiona **⌘⌥L**. Debe formatear con 4 espacios de indentación.

---

## Orden de imports

> ⚠️ Este orden está definido en `.editorconfig` (`ij_java_imports_layout`) y se aplica
> automáticamente al formatear con **⌘⌥L**. No es necesario configurarlo manualmente en IntelliJ.

```
ij_java_imports_layout = *,|,java.**,|,javax.**,|,$*,|,org.junit.**,|,jakarta.**,|,lombok.**,|,org.slf4j.**,|,org.hibernate.**,|,org.**,|,com.**
```

| Orden | Grupo          | Ejemplos                                              |
|-------|----------------|-------------------------------------------------------|
| 1     | `*`            | Cualquier import no clasificado abajo                 |
| —     | *(línea vacía)*|                                                       |
| 2     | `java.**`      | `java.util.*`, `java.math.BigDecimal`                 |
| —     | *(línea vacía)*|                                                       |
| 3     | `javax.**`     | `javax.sql.*`                                         |
| —     | *(línea vacía)*|                                                       |
| 4     | `$*`           | Imports estáticos                                     |
| —     | *(línea vacía)*|                                                       |
| 5     | `org.junit.**` | `org.junit.jupiter.api.*`                             |
| —     | *(línea vacía)*|                                                       |
| 6     | `jakarta.**`   | `jakarta.persistence.*`, `jakarta.validation.*`       |
| —     | *(línea vacía)*|                                                       |
| 7     | `lombok.**`    | `lombok.RequiredArgsConstructor`, etc.                |
| —     | *(línea vacía)*|                                                       |
| 8     | `org.slf4j.**` | `org.slf4j.Logger`, `org.slf4j.LoggerFactory`         |
| —     | *(línea vacía)*|                                                       |
| 9     | `org.hibernate.**` | `org.hibernate.*`                                 |
| —     | *(línea vacía)*|                                                       |
| 10    | `org.**`       | `org.springframework.*` y otros `org`                 |
| —     | *(línea vacía)*|                                                       |
| 11    | `com.**`       | `com.ITJobsBackend.*`                                 |

---

## Uso diario

```
1. Escribir código
2. ⌘⌥L   →  formatear
3. git commit
```
