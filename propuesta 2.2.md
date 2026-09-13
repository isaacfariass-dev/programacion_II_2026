# Propuesta de Valor y Requerimientos Funcionales : elfacha64.inc

## 1. Propuesta de valor
El Sistema Inteligente de Correlatividades y Rutas Académicas (SICRA) elimina la dependencia de la verificación manual del plan de estudios y la ambigüedad en la planificación del estudiante. El valor central del producto no es el almacenamiento estático de calificaciones, sino la predicción del bloqueo académico. el sistema actúa como un motor de análisis que expone de forma objetiva el "efecto dominó" de reprobar una asignatura clave y calcula rutas lógicas de inscripción. Esto mitiga el retraso en la carrera al obligar al estudiante a priorizar asignaturas "cuello de botella" y reduce la carga operativa del personal administrativo al automatizar el control de regularidad.

## 2. Requerimientos Funcionales

El alcance funcional se distribuye obligatoriamente en dos perfiles de acceso con permisos asimétricos.

### 2.1. Módulo de Autenticación y Seguridad
* El sistema debe validar usuario y contraseña y gestionar el estado de la sesión.
* El sistema debe restringir los casos de uso y la visibilidad de los datos de acuerdo con el rol autenticado en la sesión activa.

### 2.2. Módulo de Gestión Estructural (Usuario Administrador)
Este módulo concentra la parametrización del sistema, donde el administrador posee control operativo total.
* El sistema debe ejecutar operaciones CRUD (Creación, Lectura, Actualización, Eliminación) completas y validadas sobre el catálogo de materias.
* El sistema debe rechazar la creación de materias con códigos o nombres duplicados mediante validaciones en el controlador.
* El sistema debe permitir establecer y eliminar dependencias (correlatividades) entre materias, especificando si la exigencia es de cursada regular o de examen final aprobado.
* El sistema debe impedir la eliminación de una materia si la misma forma parte del historial académico activo de un estudiante (integridad referencial).
* El sistema debe permitir el registro y modificación del estado de aprobación (Regular, Aprobado, Ausente) en el perfil de cada estudiante.

### 2.3. Módulo de Proyección Académica (Usuario Final)
Este módulo está restringido a la lectura y al análisis algorítmico para la toma de decisiones del estudiante.
* El sistema debe evaluar el historial del estudiante contra el árbol de correlatividades y devolver un listado exacto de las materias en las que está habilitado para inscribirse en el cuatrimestre actual.
* El sistema debe diferenciar visualmente las materias que están habilitadas exclusivamente para rendir examen final de aquellas habilitadas para cursado regular.
* El sistema debe ejecutar una simulación de impacto (Efecto Dominó): el usuario selecciona una materia en curso y el sistema proyecta e imprime qué materias de los años siguientes quedarán inhabilitadas en caso de reprobación.
* El sistema debe etiquetar gráficamente las materias de "ruta crítica" (aquellas que destraban el acceso a un mayor volumen de asignaturas subsecuentes).