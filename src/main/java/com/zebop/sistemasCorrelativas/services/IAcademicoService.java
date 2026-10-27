package com.zebop.sistemasCorrelativas.services;

import com.zebop.sistemasCorrelativas.dto.PlanEstudioDTO;
import com.zebop.sistemasCorrelativas.models.enums.EstadoMateria;

/**
 * Servicio encargado de la lógica central de SICRA:
 * cálculo de correlatividades, estados de materias de un alumno
 * y actualización de historial académico.
 */
public interface IAcademicoService {

    /**
     * Calcula y retorna el plan de estudio completo de un alumno con el estado visual
     * de cada materia (aprobada, regular, habilitada o bloqueada).
     *
     * @param idAlumno Identificador del estudiante.
     * @return DTO con materias agrupadas por año y estadísticas.
     */
    PlanEstudioDTO obtenerPlanEstudio(Long idAlumno);

    /**
     * Registra o actualiza el estado académico y nota de un alumno en una materia.
     *
     * @param idAlumno    Identificador del estudiante.
     * @param idMateria   Identificador de la materia.
     * @param nuevoEstado Estado a asignar (APROBADA, REGULAR, DESAPROBADA, etc.).
     * @param notaFinal   Calificación obtenida (opcional).
     */
    void actualizarEstadoMateria(Long idAlumno, Long idMateria, EstadoMateria nuevoEstado, Double notaFinal);

    /**
     * Elimina el registro del historial académico de un alumno para una materia,
     * devolviéndola a su estado calculado (habilitada o bloqueada).
     *
     * @param idAlumno  Identificador del estudiante.
     * @param idMateria Identificador de la materia.
     */
    void resetearEstadoMateria(Long idAlumno, Long idMateria);
}
