package com.zebop.sistemasCorrelativas.dto;

import com.zebop.sistemasCorrelativas.entity.Materia;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO que envuelve una Materia junto a su estado calculado para el alumno
 * (aprobada, regular, habilitada o bloqueada) y el detalle de correlatividades.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MateriaEstadoDTO {

    private Materia materia;
    private EstadoVisualMateria estadoVisual;
    private Double notaFinal;
    private List<RequisitoInfoDTO> requisitosPrevios = new ArrayList<>();
    private List<Materia> materiasQueHabilita = new ArrayList<>();

    public boolean isAprobada() {
        return estadoVisual == EstadoVisualMateria.APROBADA;
    }

    public boolean isHabilitada() {
        return estadoVisual == EstadoVisualMateria.HABILITADA;
    }

    public boolean isBloqueada() {
        return estadoVisual == EstadoVisualMateria.BLOQUEADA;
    }

    public boolean isDesaprobada() {
        return estadoVisual == EstadoVisualMateria.DESAPROBADA;
    }

    public boolean isRegular() {
        return estadoVisual == EstadoVisualMateria.REGULAR;
    }
}
