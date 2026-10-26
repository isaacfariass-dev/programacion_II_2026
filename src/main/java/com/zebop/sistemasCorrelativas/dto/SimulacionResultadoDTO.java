package com.zebop.sistemasCorrelativas.dto;

import com.zebop.sistemasCorrelativas.models.Materia;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO que encapsula el resultado del simulador de impacto de correlatividades
 * ("Efecto Dominó" / "¿Qué pasa si apruebo X materia?").
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimulacionResultadoDTO {

    private Materia materiaSimulada;
    private List<Materia> materiasDesbloqueadas = new ArrayList<>();
    private List<Materia> materiasQueDependenDirectamente = new ArrayList<>();
    private int cantidadNuevasHabilitadas;
}
