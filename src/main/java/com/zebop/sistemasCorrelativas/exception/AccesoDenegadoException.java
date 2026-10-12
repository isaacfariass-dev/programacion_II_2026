package com.zebop.sistemasCorrelativas.exception;

/**
 * Excepción lanzada cuando un usuario autenticado intenta acceder a un recurso
 * o ejecutar una acción para la cual su rol no tiene los permisos suficientes.
 */
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }

    public AccesoDenegadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
