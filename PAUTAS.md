# Pautas del Proyecto — Gestor de Correlatividades

Proyecto: **Sistema de gestión de correlatividades de materias**
Materia: Programación II — UTN INSPT
Formato: Aplicación web tradicional (MVC), backend Java, persistencia relacional.

Este documento define las reglas de estilo, arquitectura y tecnologías a usar en el proyecto.
**Leer antes de escribir cualquier línea de código.**

---

## 1. Tecnologías a usar

- **Lenguaje**: Java (versión LTS, 17+)
- **Backend**: Servlets + JSP con Expression Language, evolucionando a **Spring Boot** si el equipo lo decide
- **Persistencia**: JDBC (`Connection`, `PreparedStatement`, `ResultSet`) contra **MySQL**
- **Vista**: motor de plantillas (JSP) o Thymeleaf si se migra a Spring Boot — HTML/CSS/JS responsive
- **Control de versiones**: Git + GitHub (repositorio único, con acceso para el docente)
- **Documentación obligatoria**: mapa del sitio, DER, diagrama UML de casos de uso

No usar frameworks ni librerías que no puedan justificarse ni explicarse en la defensa oral/escrita individual.

---

## 2. Arquitectura: MVC en capas (estilo Spring Boot)

Separar el proyecto en capas bien definidas, incluso sin usar Spring Boot todavía:

```
├── controller/     # Recibe requests, delega en service, devuelve vista
├── service/        # Lógica de negocio (validaciones, reglas de correlatividad)
├── repository/     # Acceso a datos (JDBC, queries, CRUD contra la BD)
├── model/ (bean)   # Entidades / JavaBeans: Alumno, Materia, Correlatividad, HistorialAcademico, Usuario
├── view/           # JSP / HTML — solo presentación, sin lógica de negocio
└── util/           # Excepciones custom, constantes, helpers
```

**Reglas:**
- La `view` nunca accede directamente a la base de datos ni al `repository`.
- El `controller` no contiene lógica de negocio ni queries SQL.
- El `service` no conoce detalles de HTTP (nada de `HttpServletRequest` dentro del service).
- El `repository` es el único lugar donde se escriben queries SQL.

---

## 3. Modelo de datos (DER)

Basado en el diagrama ya definido: `USUARIO`, `ALUMNO`, `MATERIA`, `HISTORIAL_ACADEMICO`, `CORRELATIVIDAD`.

Debe contener, como pide la consigna, al menos una relación de cada tipo:

| Tipo | Relación |
|---|---|
| 1:1 | `USUARIO` ↔ `ALUMNO` (tiene perfil) |
| 1:N | `ALUMNO` → `HISTORIAL_ACADEMICO`, `MATERIA` → `HISTORIAL_ACADEMICO` |
| N:M | `MATERIA` ↔ `MATERIA` a través de `CORRELATIVIDAD` (necesita / habilita) |

No modificar el modelo sin actualizar el diagrama en la documentación.

---

## 4. Roles de usuario

- **Usuario Administrador**: ABM de materias, correlatividades, gestión de alumnos.
- **Usuario Final (Alumno)**: consulta su historial académico, ve qué materias tiene habilitadas según correlatividades, no puede modificar datos maestros.

**Reglas:**
- Toda operación sensible debe validar el `rol` del usuario autenticado en la capa `service`, no solo ocultando botones en la vista.
- Manejo de sesión obligatorio (login, logout, sesión expirada) — no dejar accesos sin autenticar a rutas protegidas.

---

## 5. Reglas de código obligatorias

### ❌ No usar early returns
Cada método público debe tener **un único punto de salida** (single return). Usar variables de resultado y estructuras `if / else if / else` en vez de cortar el flujo con `return` en el medio del método.

```java
// ❌ Evitar
public String validarNota(double nota) {
    if (nota < 0) return "inválida";
    if (nota > 10) return "inválida";
    return "válida";
}

// ✅ Preferido
public String validarNota(double nota) {
    String resultado;
    if (nota < 0 || nota > 10) {
        resultado = "inválida";
    } else {
        resultado = "válida";
    }
    return resultado;
}
```

Excepción aceptada: dentro de un `try-with-resources`, un `return` dentro del `try` es válido porque el cierre del recurso está garantizado por el lenguaje.

### ✅ SOLID
- **S**: cada clase con una sola responsabilidad (ej: `MateriaRepository` solo accede a datos de `Materia`).
- **O**: extender comportamiento sin modificar clases existentes (ej: `Strategy` para tipos de validación).
- **L**: las subclases deben poder sustituir a su clase base sin romper el comportamiento esperado.
- **I**: interfaces chicas y específicas, no interfaces "todo en uno".
- **D**: depender de abstracciones (interfaces de repository/service), no de implementaciones concretas.

### ✅ Modularización
- Métodos cortos, con una sola responsabilidad clara.
- Nada de lógica de negocio, acceso a datos y presentación mezclados en una misma clase o método.
- Reutilizar código común en clases `util` o clases base, no copiar/pegar.

### ✅ Constantes
- Nada de "números mágicos" ni strings sueltos repetidos en el código.
- Declarar constantes (`public static final`) para roles (`"ADMIN"`, `"ALUMNO"`), estados (`"APROBADA"`, `"REGULAR"`, `"PENDIENTE"`), mensajes de error, tipos de correlatividad (`"NECESITA"`, `"HABILITA"`), etc.
- Centralizarlas en una clase `Constantes` o en `enum`s cuando el conjunto de valores es cerrado (preferir `enum` sobre `String` cuando sea posible).

### ✅ Comentarios
- Javadoc en clases y métodos públicos (qué hace, qué parámetros recibe, qué devuelve, qué excepciones lanza).
- Comentarios breves solo donde la lógica no es obvia — no comentar lo evidente línea por línea.

### ✅ Manejo de excepciones estricto
- No usar `catch (Exception e) {}` vacío bajo ninguna circunstancia.
- Crear excepciones propias del dominio cuando corresponda (ej: `CorrelatividadNoCumplidaException`, `MateriaNoEncontradaException`).
- Distinguir excepciones **checked** (errores recuperables, ej. `SQLException`) de **unchecked** (errores de programación).
- Nunca tragarse una excepción sin loguearla o relanzarla con contexto.
- Validar datos de entrada en el `service` antes de llegar al `repository`.

### ✅ Try-with-resources obligatorio
Todo objeto `AutoCloseable`/`Closeable` (`Connection`, `PreparedStatement`, `ResultSet`, `BufferedReader`, etc.) se abre dentro de un `try-with-resources`. Prohibido cerrar recursos manualmente en un `finally`.

```java
public Materia buscarPorId(int id) throws MateriaNoEncontradaException {
    String sql = "SELECT * FROM materia WHERE id = ?";
    try (Connection conn = DataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, id);
        try (ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                throw new MateriaNoEncontradaException("No existe materia con id " + id);
            }
            return mapearMateria(rs);
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error de acceso a datos", e);
    }
}
```

---

## 6. Checklist antes de cada commit

- [ ] ¿El método tiene un único `return`? (salvo dentro de `try-with-resources`)
- [ ] ¿La clase respeta una sola responsabilidad (SOLID - S)?
- [ ] ¿Hay números o strings mágicos que deberían ser constantes/enum?
- [ ] ¿Está la lógica de negocio separada de la vista y del acceso a datos?
- [ ] ¿Todos los recursos (`Connection`, `Statement`, `ResultSet`) usan `try-with-resources`?
- [ ] ¿Hay algún `catch` vacío o demasiado genérico?
- [ ] ¿Los métodos y clases públicas tienen Javadoc?
- [ ] ¿Se valida el rol del usuario en el `service`, no solo en la vista?

---

## 7. Recordatorio de la consigna (parcial)

1. Web tradicional con motor de plantillas.
2. Backend Java, patrón MVC.
3. CRUD completo con validaciones para al menos una entidad.
4. Manejo correcto de excepciones en operaciones críticas.
5. Persistencia relacional con relaciones 1:1, 1:N y N:M.
6. Documentación: mapa del sitio + DER.
7. Interfaz funcional, moderna y responsive.
8. Dos roles: Usuario Final y Usuario Administrador.
9. Manejo de sesión y autenticación.
10. Diagrama UML de casos de uso.
