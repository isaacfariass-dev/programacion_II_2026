package com.zebop.sistemasCorrelativas.exception;

/**
 * Excepción lanzada cuando no se encuentra un recurso solicitado
 * (Carrera, Materia, Alumno, Usuario, etc.) en la base de datos.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public RecursoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
