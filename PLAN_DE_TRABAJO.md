# Plan de Trabajo y Hoja de Ruta — SICRA

Este documento detalla los pasos a seguir, el orden de implementación por capas y el estado de avance del proyecto **SICRA (Sistema Inteligente de Correlatividades y Rutas Académicas)** para el 2do Parcial de Programación II (UTN - INSPT).

---

## 🗺️ Mapa de Dependencia entre Capas

```
[0. Configuración & DB] ──► [1. Enums & Model] ──► [2. Exceptions & Util]
                                                          │
   ┌──────────────────────────────────────────────────────┘
   ▼
[3. Repositories (JDBC)] ──► [4. Services (Negocio)] ──► [5. Controllers & Auth]
                                                                  │
   ┌──────────────────────────────────────────────────────────────┘
   ▼
[6. Views (Thymeleaf)] ──► [7. Pruebas & Algoritmos] ──► [8. Documentación Parcial]
```

---

## 📋 Lista de Tareas y Orden de Implementación

### Fase 0: Configuración Base y Entorno
- [ ] Configurar `application.properties` con la conexión a MySQL (`sicra_db`, user, pass, driver).
- [ ] Verificar la ejecución de `database.sql` en MySQL y la existencia de las 6 tablas con sus restricciones.

---

### Fase 1: Capa Model (JavaBeans) y Enums
*Ubicación:* `com.arduna.farias.servicio_correlativas.model`
- [ ] **Enums** (`model.enums`):
  - [ ] `Rol` (`ADMIN`, `ALUMNO`)
  - [ ] `EstadoMateria` (`CURSANDO`, `REGULAR`, `APROBADA`, `DESAPROBADA`)
  - [ ] `Cuatrimestre` (`ANUAL`, `PRIMER_CUATRIMESTRE`, `SEGUNDO_CUATRIMESTRE`)
  - [ ] `TipoRequisito` (`REGULAR`, `APROBADA`)
- [ ] **Entidades / JavaBeans** (`model`):
  - [ ] `Carrera.java` (idCarrera, codigo, nombre, institucion, duracionAnios)
  - [ ] `Materia.java` (idMateria, idCarrera, codigo, nombre, anioPlan, cuatrimestre, cargaHoraria)
  - [ ] `Correlatividad.java` (idMateriaDestino, idMateriaRequisito, tipoRequisito)
  - [ ] `Alumno.java` (idAlumno, idCarrera, legajo, nombre, apellido, anioIngreso)
  - [ ] `Usuario.java` (idUsuario, idAlumno, nombreUsuario, clave, rol, fechaCreacion)
  - [ ] `HistorialAcademico.java` (idHistorial, idAlumno, idMateria, estado, notaFinal, ultimaModificacion)

---

### Fase 2: Excepciones de Dominio y Utilidades
*Ubicación:* `exception` y `util`
- [ ] `Constantes.java`: Claves de sesión, mensajes estándar, nombres de vistas.
- [ ] Excepciones personalizadas:
  - [ ] `RecursoNoEncontradoException` (o específicas: `CarreraNoEncontradaException`, `MateriaNoEncontradaException`).
  - [ ] `ReglaNegocioException` / `CicloCorrelatividadException` (para detectar referencias circulares).
  - [ ] `AccesoDenegadoException` / `AutenticacionException`.

---

### Fase 3: Capa Repository (Acceso a Datos con JDBC)
*Regla:* Consultas SQL explícitas vía `DataSource`, `PreparedStatement` y `try-with-resources` (sin early returns salvo el retorno dentro del try garantizado).
* [ ] `ICarreraRepository` y `CarreraRepositoryImpl` (CRUD de carreras)
* [ ] `IMateriaRepository` y `MateriaRepositoryImpl` (CRUD materias, búsquedas por carrera y código)
* [ ] `ICorrelatividadRepository` y `CorrelatividadRepositoryImpl` (gestión de aristas de dependencias)
* [ ] `IAlumnoRepository` y `AlumnoRepositoryImpl` (gestión de alumnos y legajo)
* [ ] `IUsuarioRepository` y `UsuarioRepositoryImpl` (autenticación y búsqueda por username)
* [ ] `IHistorialAcademicoRepository` y `HistorialAcademicoRepositoryImpl` (historial, notas y estados)

---

### Fase 4: Capa Service (Lógica de Negocio y Algoritmos)
*Regla:* Todo método público con un único punto de salida (`single return`).
* [ ] `CarreraService`: Validar duplicados y restricciones al eliminar.
* [ ] `MateriaService`: Validar integridad referencial y códigos únicos.
* [ ] `CorrelatividadService`:
  - [ ] Validación de autodependencia ($A \neq A$).
  - [ ] **Detección de ciclos:** Algoritmo para evitar ciclos (ej. $A \to B \to A$).
* [ ] **`AcademicoService` (Motor SICRA / Algoritmos Clave)**:
  - [ ] Cálculo automático de materias habilitadas para cursar y rendir según historial.
  - [ ] **Simulador de Efecto Dominó:** Proyección de materias bloqueadas ante reprobación.
  - [ ] **Cálculo de Ruta Crítica:** Asignaturas que destraban mayor cantidad de correlativas posteriores.
* [ ] `AuthService`: Login, registro y validación de permisos por rol (`ADMIN` / `ALUMNO`).

---

### Fase 5: Capa Controller y Manejo de Sesión
- [ ] `AuthController`: Manejo de `/login`, `/logout`, `/registro` y sesión HTTP.
- [ ] `CarreraController` & `MateriaController`: Pantallas y formularios para Administrador.
- [ ] `AlumnoController`:
  - [ ] Vista del árbol de correlatividades por año.
  - [ ] Marcado manual de materias aprobadas/desaprobadas.
  - [ ] Simulación de efecto dominó y ruta crítica.
- [ ] Interceptor / Filtro de autenticación para control de rutas privadas por rol.

---

### Fase 6: Capa View (Plantillas Thymeleaf)
*Ubicación:* `src/main/resources/templates/`
- [ ] `fragments/` (header, navbar, footer).
- [ ] `auth/login.html` y `auth/registro.html`.
- [ ] `carrera/lista.html` y `carrera/formulario.html`.
- [ ] `materia/lista.html` y `materia/formulario.html`.
- [ ] `alumno/arbol.html`: Visualización por filas (años) y código de colores:
  - 🟩 Verde: Aprobada
  - 🟥 Rojo: Desaprobada
  - 🟨 Amarillo: Habilitada para cursar/rendir
  - ⬜ Gris: Bloqueada
- [ ] `alumno/simulador.html`: Interfaz del efecto dominó.
- [ ] Estilos CSS responsive (`static/css/estilos.css`).

---

### Fase 7: Documentación Obligatoria (Requisitos Parcial)
- [ ] **Mapa del sitio web** (diagrama de flujo/pantallas).
- [ ] **Diagrama Entidad-Relación (DER)** (fiel al script `database.sql`).
- [ ] **Diagrama UML de Casos de Uso** (con actores Administrador y Alumno).
- [ ] **Diagrama UML de Clases** (estructura POO del proyecto para la defensa escrita).
