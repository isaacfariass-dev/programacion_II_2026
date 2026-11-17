package com.zebop.sistemasCorrelativas.models.enums;

/**
 * Periodo académico en el que se dicta una materia.
 *
 * Cada año del plan se divide en dos mitades (1 = primer cuatrimestre, 2 = segundo).
 * Una materia ANUAL ocupa ambas mitades; una cuatrimestral ocupa solo una.
 * Con eso cada materia queda ubicada en una línea de tiempo continua de periodos:
 *   periodo = (anioPlan - 1) * 2 + mitad
 * lo que permite comparar materias de distintos años y cuatrimestres con un solo número.
 */
public enum Cuatrimestre {
    ANUAL("Anual", 1, 2),
    PRIMER_CUATRIMESTRE("1.er cuatrimestre", 1, 1),
    SEGUNDO_CUATRIMESTRE("2.º cuatrimestre", 2, 2);

    private final String etiqueta;
    private final int mitadInicio;
    private final int mitadFin;

    Cuatrimestre(String etiqueta, int mitadInicio, int mitadFin) {
        this.etiqueta = etiqueta;
        this.mitadInicio = mitadInicio;
        this.mitadFin = mitadFin;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /** Periodo absoluto (en cuatrimestres desde el inicio de la carrera) en el que empieza la cursada. */
    public int periodoInicio(int anioPlan) {
        return (anioPlan - 1) * 2 + mitadInicio;
    }

    /** Periodo absoluto (en cuatrimestres desde el inicio de la carrera) en el que termina la cursada. */
    public int periodoFin(int anioPlan) {
        return (anioPlan - 1) * 2 + mitadFin;
    }
}
