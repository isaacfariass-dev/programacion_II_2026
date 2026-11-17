# Mapa del sitio — Sistema de Correlativas


```
/                                            (redirige a /plan)
│
├── /plan                                   Vista principal: árbol de correlatividades y plan de estudios
│   ├── [POST] /plan/actualizar-estado      Actualiza el estado de una materia para un alumno
│   └── [POST] /plan/resetear-materia       Resetea el estado de una materia
│
├── /carreras                               Listado de carreras
│   ├── /carreras/nueva                     Formulario de alta de carrera
│   ├── /carreras/editar/{id}               Formulario de edición de carrera
│   ├── [POST] /carreras/guardar            Guarda (alta/edición) una carrera
│   └── [POST] /carreras/eliminar/{id}      Elimina una carrera
│
└── /materias                               Listado de materias
    ├── /materias/nueva                     Formulario de alta de materia
    ├── /materias/editar/{id}               Formulario de edición de materia
    ├── /materias/{id}/correlatividades     Gestión de correlatividades de una materia
    ├── [POST] /materias/guardar            Guarda (alta/edición) una materia
    ├── [POST] /materias/eliminar/{id}      Elimina una materia
    ├── [POST] /materias/{id}/correlatividades/agregar    Agrega una correlatividad
    └── [POST] /materias/{id}/correlatividades/eliminar   Elimina una correlatividad
```

## Vistas (plantillas Thymeleaf) asociadas

| Ruta                                 | Plantilla                                  |
|---------------------------------------|---------------------------------------------|
| `/plan`                               | `templates/plan/arbol.html`                 |
| `/carreras`                           | `templates/carrera/lista.html`              |
| `/carreras/nueva`, `/carreras/editar/{id}` | `templates/carrera/formulario.html`    |
| `/materias`                           | `templates/materia/lista.html`              |
| `/materias/nueva`, `/materias/editar/{id}` | `templates/materia/formulario.html`    |
| `/materias/{id}/correlatividades`     | `templates/materia/correlatividades.html`   |
| Error genérico                        | `templates/error.html`                      |
| Fragmentos comunes                    | `templates/fragments/navbar.html`, `templates/fragments/mensajes.html` |

## Notas

Actualmente existen `Usuario`, `Rol` y `UsuarioRepository` como base para una futura reimplementación, pero sin controlador ni vistas asociadas todavía.
- Las rutas `[POST]` no son páginas navegables, sino acciones que procesan formularios y redirigen a la vista de listado correspondiente.
