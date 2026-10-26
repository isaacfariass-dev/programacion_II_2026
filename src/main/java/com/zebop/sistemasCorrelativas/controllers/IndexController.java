package com.zebop.sistemasCorrelativas.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controlador de inicio que redirige directamente a la vista principal
 * del plan de estudios y árbol de correlatividades.
 */
@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "redirect:/plan";
    }

    @GetMapping("/acceso-denegado")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String accesoDenegado() {
        return "acceso-denegado";
    }
}
