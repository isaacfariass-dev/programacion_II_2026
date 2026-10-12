package com.zebop.sistemasCorrelativas.exception;

/**
 * Excepción lanzada cuando la adición de una correlatividad provocaría
 * una dependencia cíclica o recursiva (ej. A depende de B y B de A, o A depende de A).
 */
public class CorrelatividadCiclicaException extends ReglaNegocioException {

    public CorrelatividadCiclicaException(String mensaje) {
        super(mensaje);
    }

    public CorrelatividadCiclicaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
