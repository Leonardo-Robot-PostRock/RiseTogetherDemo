# Estilo de Código — ITJobs Backend

## Formatter: Google Java Format (estilo AOSP)

El proyecto usa **Google Java Format** con estilo **AOSP** como convención de formato:

- **4 espacios** de indentación (AOSP = Android Open Source Project; el estilo `GOOGLE` usa 2 espacios).
- El plugin oficial **Google Java Format** para IntelliJ usa el mismo formatter.
- El shortcut **⌘⌥L** (macOS) / **Ctrl+Alt+L** (Windows/Linux) aplica el formato directamente en el IDE.

---

## Configuración del plugin de IntelliJ (onboarding)

### 1. Configurar la JRE de IntelliJ (requerido para el plugin)

El plugin `google-java-format` requiere acceso a clases internas del compilador. Hacerlo:

1. Ve a **Help → Edit Custom VM Options...**
2. Pega estas líneas al final del archivo (crea uno si no existe):

```
--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
```

3. **Reinicia IntelliJ** completamente (no solo el proyecto).

Sin esto, el plugin no funcionará.

### 2. Instalar el plugin

`Settings > Plugins` → buscar **"Google Java Format"** → instalar → reiniciar IntelliJ.

### 3. Activar y configurar estilo AOSP

`Settings > Other Settings > Google Java Format`

| Opción                    | Valor                 |
|---------------------------|-----------------------|
| Enable Google Java Format | ✅ activado            |
| Code style                | **AOSP** (4 espacios) |

> ⚠️ El estilo por defecto del plugin es `Google` (2 espacios). Cambiar a **AOSP**.

### 4. Verificar que funciona

1. Abre cualquier archivo `.java`.
2. Formatea con **⌘⌥L** (macOS) o **Ctrl+Alt+L** (Windows/Linux).
3. Revisa que la indentación sea de 4 espacios.

---

## Orden de imports

| Grupo     | Ejemplos                                        |
|-----------|-------------------------------------------------|
| `java`    | `java.util.*`, `java.math.BigDecimal`           |
| `javax`   | `javax.sql.*`                                   |
| `jakarta` | `jakarta.persistence.*`, `jakarta.validation.*` |
| `org`     | `org.springframework.*`, `org.junit.*`          |
| `com`     | `com.ITJobsBackend.*`                           |

Configura este orden en IntelliJ:
`Settings > Editor > Code Style > Java > Imports` → ajustar el orden de grupos como arriba.

---

## Flujo de trabajo recomendado

```
1. Escribir código
2. ⌘⌥L  →  aplicar formato (Google Java Format plugin)
3. git add / git commit
```
