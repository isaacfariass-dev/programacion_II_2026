# Registro de Correcciones Resueltas

Este documento registra el historial de problemas, discrepancias y correcciones aplicadas sobre la base de código del proyecto **SistemasCorrelativas (SICRA)**.

---

## [2026-10-07] — Corrección del Paquete de Entidades y Modelado JPA

### 1. Corrección en Enums (`entity/enums/`)
- **Problema detectado:** Los archivos `Rol.java`, `Cuatrimestre.java`, `EstadoMateria.java` y `TipoRequisito.java` se crearon en la carpeta `entity/enums/` pero carecían de la sentencia `package com.zebop.sistemasCorrelativas.entity.enums;`. Esto provocaba que Java los interpretara como pertenecientes al paquete por defecto y fallara la compilación al intentar importarlos desde otras clases.
- **Solución aplicada:** Se agregó la cabecera de paquete formal y Javadoc explicativo a los 4 enums.

---

### 2. Implementación de `Alumno.java`
- **Problema detectado:** El archivo `Alumno.java` se encontraba completamente vacío (0 bytes), impidiendo la referencia al estudiante tanto en el módulo de usuarios como en el historial académico.
- **Solución aplicada:** Se implementó la clase JavaBean completa con sus atributos (`idAlumno`, `carrera`, `legajo`, `nombre`, `apellido`, `anioIngreso`), constructores (por defecto y parametrizado), getters, setters, `equals`, `hashCode` y `toString`.

---

### 3. Consistencia de Tipos con Enums en las Entidades
- **Problemas detectados:**
  - `Materia.java` definía el cuatrimestre como `Integer` en lugar del enum `Cuatrimestre`.
  - `Correlatividad.java` definía el tipo de requisito como `String` en lugar del enum `TipoRequisito`.
  - `HistorialAcademico.java` definía el estado como `String` en lugar del enum `EstadoMateria`.
- **Solución aplicada:** Se actualizaron los atributos, parámetros de constructor, getters y setters para utilizar fuertemente los enums creados, garantizando la seguridad de tipos y evitando strings mágicos.

---

### 4. Mapeo Objeto-Relacional con Spring Data JPA
- **Problema detectado:** Las entidades eran clases POJO sin metadatos de persistencia, por lo cual Spring Data JPA / Hibernate no podía reconocer las tablas ni sus relaciones en MySQL.
- **Solución aplicada:** Se incorporaron las anotaciones estándar de JPA:
  - `@Entity` y `@Table(name = "...")` en todas las clases del modelo.
  - `@Id` y `@GeneratedValue(strategy = GenerationType.IDENTITY)` para las claves primarias autonuméricas.
  - `@Enumerated(EnumType.STRING)` para persistir los valores de enums como cadenas legibles en la base de datos (`ADMIN`, `APROBADA`, etc.).
  - Mapeo de relaciones explícitas:
    - **1:1**: `@OneToOne` en `Usuario` hacia `Alumno` (`id_alumno` con restricción única).
    - **1:N**: `@ManyToOne` en `Materia` hacia `Carrera`, y en `Alumno` hacia `Carrera`.
    - **1:N**: `@ManyToOne` en `HistorialAcademico` hacia `Alumno` y `Materia`.
    - **N:M (Recursiva)**: Mapeo de `Correlatividad` con clave compuesta mediante `@IdClass(CorrelatividadId.class)` vinculando `materiaDestino` y `materiaRequisito`.

---

### 5. Verificación de Compilación
- Se ejecutó `./mvnw compile` con Maven 3.9 y Java 21: **BUILD SUCCESS**.
