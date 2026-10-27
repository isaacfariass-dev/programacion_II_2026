package com.zebop.sistemasCorrelativas.util;

/**
 * Clase utilitaria que centraliza las constantes globales del sistema.
 * Evita la dispersión de valores fijos, cadenas mágicas y números mágicos en el código.
 */
public final class Constantes {

    // Constructor privado para impedir la instanciación de una clase utilitaria
    private Constantes() {
    }

    // -------------------------------------------------------------
    // Mensajes de Error - Recursos no encontrados
    // -------------------------------------------------------------
    public static final String ERROR_CARRERA_NO_ENCONTRADA = "No se encontró la carrera especificada.";
    public static final String ERROR_MATERIA_NO_ENCONTRADA = "No se encontró la materia especificada.";
    public static final String ERROR_ALUMNO_NO_ENCONTRADA = "No se encontró el alumno especificado.";
    public static final String ERROR_CORRELATIVIDAD_NO_ENCONTRADA = "No se encontró la correlatividad especificada.";
    public static final String ERROR_HISTORIAL_NO_ENCONTRADO = "No se encontró el registro de historial académico.";

    // -------------------------------------------------------------
    // Mensajes de Error - Reglas de Negocio y Correlatividades
    // -------------------------------------------------------------
    public static final String ERROR_AUTODEPENDENCIA = "Una materia no puede ser correlativa de sí misma.";
    public static final String ERROR_CICLO_CORRELATIVIDAD = "La correlatividad genera una dependencia circular en el plan de estudios.";
    public static final String ERROR_CORRELATIVIDAD_DUPLICADA = "La relación de correlatividad ya se encuentra registrada.";
    public static final String ERROR_CARRERAS_DISTINTAS = "Las materias de una correlatividad deben pertenecer a la misma carrera.";
    public static final String ERROR_LEGAJO_DUPLICADO = "El número de legajo ya se encuentra asignado a otro alumno.";
    public static final String ERROR_CODIGO_MATERIA_DUPLICADO = "El código de materia ya existe en el sistema.";
    public static final String ERROR_CODIGO_CARRERA_DUPLICADO = "El código de carrera ya existe en el sistema.";

    // -------------------------------------------------------------
    // Valores de Dominio y Validación Numérica
    // -------------------------------------------------------------
    public static final int CARGA_HORARIA_MINIMA = 1;
    public static final int DURACION_CARRERA_MINIMA_ANIOS = 1;
    public static final double NOTA_MINIMA = 1.0;
    public static final double NOTA_MAXIMA = 10.0;

    // -------------------------------------------------------------
    // Nombres de Vistas Comunes
    // -------------------------------------------------------------
    public static final String VISTA_ERROR = "error";
}
