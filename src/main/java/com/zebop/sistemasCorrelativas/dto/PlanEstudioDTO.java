package com.zebop.sistemasCorrelativas.dto;

import com.zebop.sistemasCorrelativas.entity.Alumno;
import com.zebop.sistemasCorrelativas.entity.Carrera;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO que agrupa toda la información del plan de estudio de un alumno,
 * sus materias ordenadas por año académico y estadísticas de avance.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanEstudioDTO {

    private Alumno alumno;
    private Carrera carrera;
    private Map<Integer, List<MateriaEstadoDTO>> materiasPorAnio = new LinkedHashMap<>();

    private int totalMaterias;
    private int materiasAprobadas;
    private int materiasRegulares;
    private int materiasHabilitadas;
    private int materiasBloqueadas;
    private int materiasDesaprobadas;
    private double promedioNotas;
    private double porcentajeAvance;
}
