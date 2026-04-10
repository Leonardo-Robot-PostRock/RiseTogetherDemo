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

Configura en `Settings > Editor > Code Style > Java > Imports`:

| Orden | Grupo     | Ejemplos                                        |
|-------|-----------|-------------------------------------------------|
| 1     | `java`    | `java.util.*`, `java.math.BigDecimal`           |
| 2     | `javax`   | `javax.sql.*`                                   |
| 3     | `jakarta` | `jakarta.persistence.*`, `jakarta.validation.*` |
| 4     | `org`     | `org.springframework.*`, `org.junit.*`          |
| 5     | `com`     | `com.ITJobsBackend.*`                           |

---

## Uso diario

```
1. Escribir código
2. ⌘⌥L   →  formatear
3. git commit
```
