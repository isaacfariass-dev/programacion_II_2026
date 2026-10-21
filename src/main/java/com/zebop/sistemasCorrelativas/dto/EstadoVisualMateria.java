package com.zebop.sistemasCorrelativas.dto;

/**
 * Enumeración que define el estado visual calculado para una materia
 * en la vista del plan de estudios del alumno.
 */
public enum EstadoVisualMateria {
    APROBADA("Aprobada", "estado-aprobada", "bg-success text-white"),
    REGULAR("Regular", "estado-regular", "bg-info text-white"),
    DESAPROBADA("Desaprobada", "estado-desaprobada", "bg-danger text-white"),
    HABILITADA("Habilitada", "estado-habilitada", "bg-warning text-dark"),
    BLOQUEADA("Bloqueada", "estado-bloqueada", "bg-secondary text-white");

    private final String etiqueta;
    private final String claseCss;
    private final String badgeBootstrap;

    EstadoVisualMateria(String etiqueta, String claseCss, String badgeBootstrap) {
        this.etiqueta = etiqueta;
        this.claseCss = claseCss;
        this.badgeBootstrap = badgeBootstrap;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getClaseCss() {
        return claseCss;
    }

    public String getBadgeBootstrap() {
        return badgeBootstrap;
    }
}
