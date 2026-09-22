# Requisitos Funcionales — Gestor de Correlatividades

Proyecto: **Sistema de gestión de correlatividades de materias**
Documento complementario a `PAUTAS.md`.

---

## 1. Actores del sistema

| Actor | Descripción |
|---|---|
| **Usuario Administrador** | Carga y mantiene la información académica de la carrera (materias, años, correlatividades). |
| **Usuario Final (Alumno)** | Consulta su carrera, visualiza el árbol de correlativas y marca el estado de sus materias. |

---

## 2. Módulo: Carreras

### RF-01 — Listado de carreras
El sistema debe permitir ver un listado de todas las carreras cargadas, cada una con su información básica (nombre, duración, institución, cantidad de materias).

### RF-02 — Ficha de carrera
Al seleccionar una carrera se debe mostrar su información básica y dar acceso al árbol de correlatividades de esa carrera.

### RF-03 — Alta/baja/modificación de carrera (Admin)
El administrador debe poder crear, editar y eliminar carreras, cargando al menos: nombre, institución, duración en años/cuatrimestres.

---

## 3. Módulo: Plan de estudios / Árbol de correlatividades

### RF-04 — Organización por año
Las materias de una carrera deben visualizarse agrupadas por año, en filas (una fila = un año).

### RF-05 — Vista de árbol
Dentro de cada fila, las materias deben mostrarse conectadas verticalmente mediante líneas (estilo árbol/diagrama), uniendo cada materia con:
- Sus **correlativas anteriores** (materias que la habilitan).
- Sus **correlativas posteriores** (materias a las que habilita).

### RF-06 — Estado visual por color
Cada materia debe mostrarse con un color según su estado:

| Color | Estado |
|---|---|
| 🟩 Verde | Aprobada |
| 🟥 Rojo | Reprobada / desaprobada |
| 🟨 Amarillo (o similar) | Habilitada para cursar el próximo período |
| ⬜ Gris | No habilitada (correlativas pendientes) |

### RF-07 — Marcado de estado por el alumno
El alumno debe poder marcar manualmente cada materia como **aprobada** o **reprobada** desde su vista personal. Este cambio debe:
- Guardarse en su historial académico.
- Recalcular automáticamente qué materias quedan habilitadas o no, en base a las correlatividades cargadas.

### RF-08 — Cálculo automático de habilitación
El sistema debe determinar automáticamente, para cada materia no cursada, si el alumno cumple las correlativas necesarias (según tipo: regularidad o aprobación) y reflejarlo en el color correspondiente.

### RF-09 — Detalle de una materia
Al seleccionar una materia en el árbol se debe poder ver: nombre, código, año, cuatrimestre, carga horaria, correlativas que necesita y materias que habilita.

---

## 4. Módulo: Administración de plan de estudios (Admin)

### RF-10 — Carga de materias
El administrador debe poder cargar materias de una carrera indicando: nombre, código, año, cuatrimestre, carga horaria.

### RF-11 — Carga de correlatividades
El administrador debe poder definir, para cada materia, qué otras materias necesita como correlativas (y de qué tipo: para cursar / para rendir final) y qué materias habilita.

### RF-12 — Edición y eliminación
El administrador debe poder editar o eliminar materias y correlatividades ya cargadas, con validación para no dejar referencias rotas (ej: no eliminar una materia que es correlativa de otra sin antes resolver esa dependencia).

### RF-13 — Validación de ciclos
El sistema no debe permitir cargar correlatividades que generen ciclos (ej: Materia A necesita B, B necesita A).

---

## 5. Módulo: Usuarios y sesión

### RF-14 — Registro y login
El sistema debe permitir registrar alumnos y administradores, y autenticarlos mediante usuario/contraseña.

### RF-15 — Permisos por rol
- El **alumno** solo puede ver y modificar su propio historial académico.
- El **administrador** puede modificar el plan de estudios de cualquier carrera, pero no el historial académico de un alumno particular (salvo que se defina un caso de uso específico para eso).

---

## 6. Ideas / funcionalidades futuras (no obligatorias para la primera entrega)

### RFI-01 — Importación de plan de estudios desde archivo
Permitir al administrador subir un archivo (PDF, Word o Excel) con el plan de estudios de una carrera, y que el sistema:
1. Extraiga el texto/tabla del archivo.
2. Identifique materias, año, cuatrimestre y correlatividades mencionadas.
3. Genere automáticamente el árbol de correlativas, dejando al administrador revisar y confirmar antes de guardar.

**Notas técnicas a considerar si se implementa:**
- Requiere parseo de PDF (ej. librería de extracción de texto/tablas) y de Excel (ej. Apache POI) y Word (ej. Apache POI también).
- El resultado de la extracción es **propuesto**, no definitivo: siempre debe haber una pantalla de revisión manual antes de persistir en la base de datos, porque el formato de los planes de estudio varía mucho entre carreras.
- Conviene tratarlo como un módulo aparte (`ImportadorPlanEstudio`), sin mezclarlo con la lógica normal de carga manual (RF-10/RF-11), para no complejizar el CRUD base.

### RFI-02 — Exportación del árbol de correlativas
Exportar la vista del árbol de una carrera a PDF o imagen, para que el alumno la pueda guardar o imprimir.

### RFI-03 — Simulador "qué pasa si..."
Permitir al alumno simular la aprobación de una materia sin guardarla, para ver qué otras materias se habilitarían, antes de confirmar el cambio real.

---

## 7. Resumen de prioridad para el parcial

| Prioridad | Requisitos |
|---|---|
| **Obligatorios (entrega base)** | RF-01 a RF-15 |
| **Deseables (si sobra tiempo)** | RFI-02, RFI-03 |
| **Extra / diferencial** | RFI-01 (importación desde PDF/Word/Excel) |
