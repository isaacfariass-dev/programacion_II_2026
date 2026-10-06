# Gestión y Visualización de Correlatividades Sin Grafos

Este documento explica cómo consultar, visualizar y validar el sistema de correlatividades de **SICRA** de manera directa, práctica y relacional, sin necesidad de recurrir a estructuras de datos complejas ni librerías de grafos.

---

## 1. La Realidad en la Base de Datos

En el modelo relacional, las correlatividades son simplemente registros en una tabla asociativa (`correlatividad`). Cada fila representa un vínculo directo de dependencia:

$$(\text{materia\_destino}, \text{materia\_requisito}, \text{tipo\_requisito})$$

No se almacenan grafos en la base de datos; solo pares de IDs. Por lo tanto, cualquier consulta se resuelve con sentencias SQL estándar y `JOIN`s directos.

---

## 2. Consultas SQL Directas (Sin Recorridos Recursivos)

### A. ¿Qué materias necesito para cursar una materia específica? (Requisitos Previos)
Para saber qué requisitos exige una asignatura (por ejemplo, *Programación II* con `id_materia = 2`), basta con consultar las materias requisito directas:

```sql
SELECT 
    m_req.id_materia,
    m_req.codigo,
    m_req.nombre,
    c.tipo_requisito
FROM correlatividad c
JOIN materia m_req ON c.id_materia_requisito = m_req.id_materia
WHERE c.id_materia_destino = 2;
```

**Resultado:** Una lista plana de asignaturas que el estudiante debe tener regulares o aprobadas.

---

### B. ¿Qué materias puedo cursar si apruebo esta materia? (Materias que Habilita)
Para saber qué asignaturas se desbloquean al aprobar una materia (por ejemplo, *Programación I* con `id_materia = 1`):

```sql
SELECT 
    m_dest.id_materia,
    m_dest.codigo,
    m_dest.nombre,
    c.tipo_requisito
FROM correlatividad c
JOIN materia m_dest ON c.id_materia_destino = m_dest.id_materia
WHERE c.id_materia_requisito = 1;
```

**Resultado:** Una lista plana de materias futuras que dependen de la actual.

---

## 3. Lógica de Negocio: Validación de Habilitación de un Alumno

Para determinar si un alumno está en condiciones de cursar una materia, **no hace falta recorrer el historial completo hacia atrás ni recorrer un árbol**:

1. Se obtienen los **requisitos directos** de la materia de destino en `correlatividad`.
2. Si la materia no tiene requisitos previos (ej. materias de 1° año), queda **Habilitada** inmediatamente.
3. Si tiene requisitos, se cruzan contra el `historial_academico` del alumno:
   * Si el requisito pide `REGULAR`, el alumno debe tener estado `REGULAR` o `APROBADA`.
   * Si el requisito pide `APROBADA`, el alumno debe tener estado `APROBADA`.
4. Si se cumplen todos los requisitos directos $\rightarrow$ **Habilitada**. De lo contrario $\rightarrow$ **Bloqueada**.

> **¿Por qué alcanza con validar solo los requisitos directos?**  
> Porque las reglas académicas son transitivas por definición: si el alumno ya aprobó *Programación II*, ya debió haber cumplido en su momento los requisitos de *Programación I*. No es necesario revalidar la historia previa.

---

## 4. Alternativas de Visualización en la Interfaz (UI) Sin Grafos

No es obligatorio implementar diagramas interactivos con nodos y flechas (estilo Canvas, SVG o D3.js). Existen alternativas estándar muy usadas en sistemas de gestión académica:

### 1. Plan de Estudio Tabular (Formato Oficial Universitario)
Una grilla clásica ordenada por año y cuatrimestre:

| Año | Código | Asignatura | Correlativa para Cursar | Correlativa para Rendir Final |
| :---: | :---: | :--- | :--- | :--- |
| **1°** | `PROG-1` | Programación I | — | — |
| **1°** | `ARQ-1` | Arquitectura y Sistemas | — | — |
| **2°** | `PROG-2` | Programación II | Programación I (Regular) | Programación I (Aprobada) |
| **2°** | `BD-1` | Bases de Datos | Programación I (Regular) | Programación I (Aprobada) |
| **3°** | `PROG-3` | Programación III | Programación II (Regular) | Programación II (Aprobada), BD-1 (Aprobada) |

---

### 2. Tarjetas / Acordeones por Año con Semáforo
Las materias se presentan en bloques colapsables por año lectivo (1° Año, 2° Año, 3° Año) y con un código de colores según el estado del estudiante:

* 🟢 **Verde:** Aprobada.
* 🟡 **Amarillo:** Habilitada para cursar (cumple correlativas directas).
* ⚪ **Gris / Bloqueada:** No habilitada (muestra la lista de materias pendientes).
* 🔴 **Rojo:** Desaprobada.

---

### 3. Ficha / Modal de Detalle de Materia
Al hacer clic sobre una materia, se abre una vista lateral o modal que muestra de forma limpia:

```text
┌──────────────────────────────────────────────────────────┐
│ PROGRAMACIÓN II (Código: PROG-2)                         │
│ Año: 2° | Carga Horaria: 64 hs                           │
├──────────────────────────────────────────────────────────┤
│ Requisitos para Cursar:                                  │
│   [✓] Programación I (Aprobada)                          │
│                                                          │
│ Materias que Habilita a Futuro:                          │
│   • Programación III                                     │
│   • Taller de Práctica Profesionalizante                 │
│                                                          │
│ Estado actual del alumno: HABILITADA PARA CURSAR         │
└──────────────────────────────────────────────────────────┘
```

---

## 5. ¿Cuándo sí se utiliza la teoría de grafos?

Prescindir de grafos simplifica el desarrollo y la interfaz. Los únicos dos casos donde el concepto de grafo cobra relevancia son:

1. **Detección de dependencias circulares al cargar datos (Backend):**  
   Para evitar que un administrador cargue por error que *A requiere B* y luego que *B requiere A*. Esto se resuelve fácilmente con un chequeo de ciclos antes de guardar una nueva correlatividad.
2. **Visualización estética opcional:**  
   Únicamente si se desea como valor agregado una pantalla interactiva con líneas que conecten cajas al estilo de un diagrama de flujo.
