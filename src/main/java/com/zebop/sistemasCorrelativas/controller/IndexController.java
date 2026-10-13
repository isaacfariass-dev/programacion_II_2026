package com.zebop.sistemasCorrelativas.controller;

import com.zebop.sistemasCorrelativas.entity.Alumno;
import com.zebop.sistemasCorrelativas.service.AlumnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {
    @Autowired
private AlumnoService aluservicio;

@GetMapping("/")
public String index(Model model) {
    List<Alumno> alumnado = aluservicio.obtenerAlumnos(); 
model.addAttribute("alumnos", alumnado);
return "index";
}
}