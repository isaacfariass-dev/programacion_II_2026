package com.zebop.sistemasCorrelativas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.zebop.sistemasCorrelativas.util.Constantes;

/**
 * Manejador global de excepciones para toda la capa Controller.
 * Intercepta los errores de negocio, autenticación y recursos no encontrados
 * para presentar vistas de error amigables en lugar del stacktrace técnico por defecto.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String manejarRecursoNoEncontrado(RecursoNoEncontradoException ex, Model model) {
        String vistaResultado;
        model.addAttribute("tituloError", "Recurso no encontrado");
        model.addAttribute("mensajeError", ex.getMessage());
        model.addAttribute("codigoEstado", 404);
        vistaResultado = Constantes.VISTA_ERROR;
        return vistaResultado;
    }

    /**
     * Rutas inexistentes: Spring lanza NoResourceFoundException (o NoHandlerFoundException),
     * que de otro modo caería en el handler genérico y se mostraría como 500.
     */
    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String manejarRutaInexistente(Exception ex, Model model) {
        String vistaResultado;
        model.addAttribute("tituloError", "Página no encontrada");
        model.addAttribute("mensajeError", "La dirección que buscás no existe o fue movida.");
        model.addAttribute("codigoEstado", 404);
        vistaResultado = Constantes.VISTA_ERROR;
        return vistaResultado;
    }

    @ExceptionHandler(ReglaNegocioException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String manejarReglaNegocio(ReglaNegocioException ex, Model model) {
        String vistaResultado;
        model.addAttribute("tituloError", "Regla de Negocio no Válida");
        model.addAttribute("mensajeError", ex.getMessage());
        model.addAttribute("codigoEstado", 400);
        vistaResultado = Constantes.VISTA_ERROR;
        return vistaResultado;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String manejarArgumentoInvalido(IllegalArgumentException ex, Model model) {
        String vistaResultado;
        model.addAttribute("tituloError", "Dato Inválido");
        model.addAttribute("mensajeError", ex.getMessage());
        model.addAttribute("codigoEstado", 400);
        vistaResultado = Constantes.VISTA_ERROR;
        return vistaResultado;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String manejarExcepcionGeneral(Exception ex, Model model) {
        String vistaResultado;
        model.addAttribute("tituloError", "Error Inesperado del Sistema");
        model.addAttribute("mensajeError", "Ocurrió un error inesperado al procesar la solicitud: " + ex.getMessage());
        model.addAttribute("codigoEstado", 500);
        vistaResultado = Constantes.VISTA_ERROR;
        return vistaResultado;
    }
}
