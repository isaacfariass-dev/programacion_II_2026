package com.zebop.sistemasCorrelativas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
}
