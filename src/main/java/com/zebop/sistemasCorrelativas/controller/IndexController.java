package com.zebop.sistemasCorrelativas.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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