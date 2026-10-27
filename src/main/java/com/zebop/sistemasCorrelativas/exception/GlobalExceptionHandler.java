package com.zebop.sistemasCorrelativas.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.zebop.sistemasCorrelativas.util.Constantes;

/**
 * Manejador global de excepciones para toda la capa Controller.
 * Intercepta los errores de negocio, autenticación y recursos no encontrados
 * para presentar vistas de error amigables en lugar del stacktrace técnico por defecto.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public String manejarRecursoNoEncontrado(RecursoNoEncontradoException ex, Model model) {
        String vistaResultado;
        model.addAttribute("tituloError", "Recurso no encontrado");
        model.addAttribute("mensajeError", ex.getMessage());
        model.addAttribute("codigoEstado", 404);
        vistaResultado = Constantes.VISTA_ERROR;
        return vistaResultado;
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public String manejarReglaNegocio(ReglaNegocioException ex, Model model) {
        String vistaResultado;
        model.addAttribute("tituloError", "Regla de Negocio no Válida");
        model.addAttribute("mensajeError", ex.getMessage());
        model.addAttribute("codigoEstado", 400);
        vistaResultado = Constantes.VISTA_ERROR;
        return vistaResultado;
    }

    @ExceptionHandler(Exception.class)
    public String manejarExcepcionGeneral(Exception ex, Model model) {
        String vistaResultado;
        model.addAttribute("tituloError", "Error Inesperado del Sistema");
        model.addAttribute("mensajeError", "Ocurrió un error inesperado al procesar la solicitud: " + ex.getMessage());
        model.addAttribute("codigoEstado", 500);
        vistaResultado = Constantes.VISTA_ERROR;
        return vistaResultado;
    }
}
