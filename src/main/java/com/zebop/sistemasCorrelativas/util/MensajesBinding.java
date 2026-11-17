package com.zebop.sistemasCorrelativas.util;

import org.springframework.beans.TypeMismatchException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * Traduce los errores que ocurren al enlazar un formulario con una entidad (@ModelAttribute)
 * a un mensaje legible para el usuario.
 *
 * Las entidades validan en sus setters lanzando IllegalArgumentException; Spring llama a esos
 * setters durante el binding (antes de entrar al método del controller) y guarda la excepción
 * dentro del BindingResult. Esta clase recupera el mensaje original de esa excepción.
 */
public final class MensajesBinding {

    private MensajesBinding() {
    }

    public static String primerMensaje(BindingResult binding) {
        ObjectError error = binding.getAllErrors().get(0);
        String mensaje = "Hay datos inválidos en el formulario.";

        if (error.contains(TypeMismatchException.class)) {
            String campo = (error instanceof FieldError fe) ? fe.getField() : error.getObjectName();
            mensaje = "El valor ingresado en el campo '" + campo + "' no es válido.";
        } else if (error.contains(Throwable.class)) {
            Throwable causa = error.unwrap(Throwable.class);
            while (causa.getCause() != null) {
                causa = causa.getCause();
            }
            if (causa.getMessage() != null) {
                mensaje = causa.getMessage();
            }
        } else if (error.getDefaultMessage() != null) {
            mensaje = error.getDefaultMessage();
        }
        return mensaje;
    }
}
