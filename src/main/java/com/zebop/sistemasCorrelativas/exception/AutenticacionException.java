package com.zebop.sistemasCorrelativas.exception;

/**
 * Excepción lanzada cuando fallan las credenciales de inicio de sesión
 * o se intenta acceder a una sección protegida sin sesión activa.
 */
public class AutenticacionException extends RuntimeException {

    public AutenticacionException(String mensaje) {
        super(mensaje);
    }

    public AutenticacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
