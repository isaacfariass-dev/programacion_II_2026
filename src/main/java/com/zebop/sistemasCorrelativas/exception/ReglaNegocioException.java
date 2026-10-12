package com.zebop.sistemasCorrelativas.exception;

/**
 * Excepción base lanzada cuando una operación viola una regla de negocio
 * del dominio académico (por ejemplo: correlatividades no cumplidas,
 * inscripción inválida, duplicación lógica, etc.).
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }

    public ReglaNegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
