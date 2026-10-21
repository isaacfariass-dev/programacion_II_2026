package com.zebop.sistemasCorrelativas.controller;

import com.zebop.sistemasCorrelativas.entity.Alumno;
import com.zebop.sistemasCorrelativas.service.IAlumnoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    private final IAlumnoService alumnoService;

    public IndexController(IAlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Alumno> alumnado = alumnoService.obtenerTodos();
        model.addAttribute("alumnos", alumnado);
        return "index";
    }
}