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
    public static final String ERROR_NOMBRE_USUARIO_DUPLICADO = "El nombre de usuario ya se encuentra en uso.";
    public static final String ERROR_USUARIO_NO_ENCONTRADO = "No se encontró el usuario especificado.";
    public static final String ERROR_CODIGO_MATERIA_DUPLICADO = "El código de materia ya existe en el sistema.";
    public static final String ERROR_NOMBRE_MATERIA_DUPLICADO = "Ya existe una materia con ese nombre en la carrera.";
    public static final String ERROR_CODIGO_CARRERA_DUPLICADO = "El código de carrera ya existe en el sistema.";
    public static final String ERROR_NOMBRE_CARRERA_DUPLICADO = "Ya existe una carrera registrada con ese nombre.";
    public static final String ERROR_REQUISITO_PERIODO_INVALIDO = "La materia requisito debe terminar de cursarse antes de que empiece la materia que la exige (revisá año y cuatrimestre de ambas).";
    public static final String ERROR_ANIO_FUERA_DE_CARRERA = "La carrera dura %d años: el año del plan de la materia no puede ser mayor.";
    public static final String ERROR_DURACION_MENOR_QUE_MATERIAS = "No se puede reducir la duración a %d años: hay materias cargadas en años posteriores. Movelas o eliminalas primero.";
    public static final String ERROR_CAMBIO_CARRERA_CON_CORRELATIVAS = "No se puede cambiar la carrera de la materia porque tiene correlatividades cargadas. Quitalas primero.";
    public static final String ERROR_CAMBIO_PERIODO_ROMPE_CORRELATIVAS = "No se puede cambiar el año/cuatrimestre: la materia quedaría superpuesta o desordenada respecto de sus correlativas (%s). Quitá esas correlatividades primero.";

    // -------------------------------------------------------------
    // Valores de Dominio y Validación Numérica
    // -------------------------------------------------------------
    public static final int CARGA_HORARIA_MINIMA = 1;
    public static final int DURACION_CARRERA_MINIMA_ANIOS = 1;
    public static final int DURACION_CARRERA_MAXIMA_ANIOS = 10;
    public static final int ANIO_PLAN_MINIMO = 1;
    // Límite absoluto; el límite real de cada materia es la duración de su carrera (se valida en MateriaServiceImpl).
    public static final int ANIO_PLAN_MAXIMO = DURACION_CARRERA_MAXIMA_ANIOS;
    public static final double NOTA_MINIMA = 1.0;
    public static final double NOTA_MAXIMA = 10.0;

    public static final int LONGITUD_MAXIMA_CODIGO_CARRERA = 20;
    public static final int LONGITUD_MAXIMA_NOMBRE_CARRERA = 100;
    public static final int LONGITUD_MAXIMA_INSTITUCION = 100;
    public static final int LONGITUD_MAXIMA_CODIGO_MATERIA = 20;
    public static final int LONGITUD_MAXIMA_NOMBRE_MATERIA = 100;
    public static final int LONGITUD_MAXIMA_LEGAJO = 20;
    public static final int LONGITUD_MAXIMA_NOMBRE_ALUMNO = 50;
    public static final int LONGITUD_MAXIMA_APELLIDO_ALUMNO = 50;
    public static final int LONGITUD_MAXIMA_NOMBRE_USUARIO = 50;
    public static final int LONGITUD_MAXIMA_CLAVE = 255;

    // -------------------------------------------------------------
    // Nombres de Vistas Comunes
    // -------------------------------------------------------------
    public static final String VISTA_ERROR = "error";
}
