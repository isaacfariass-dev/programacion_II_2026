package com.zebop.sistemasCorrelativas.controller;

import com.zebop.sistemasCorrelativas.entity.Carrera;
import com.zebop.sistemasCorrelativas.exception.RecursoNoEncontradoException;
import com.zebop.sistemasCorrelativas.exception.ReglaNegocioException;
import com.zebop.sistemasCorrelativas.service.ICarreraService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador para la gestión y ABM de Carreras.
 */
@Controller
@RequestMapping("/carreras")
public class CarreraController {

    private final ICarreraService carreraService;

    public CarreraController(ICarreraService carreraService) {
        this.carreraService = carreraService;
    }

    @GetMapping
    public String listar(Model model) {
        List<Carrera> carreras = carreraService.obtenerTodas();
        model.addAttribute("carreras", carreras);
        return "carrera/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("carrera", new Carrera());
        model.addAttribute("esEdicion", false);
        return "carrera/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        String vista;
        try {
            Carrera carrera = carreraService.obtenerPorId(id);
            model.addAttribute("carrera", carrera);
            model.addAttribute("esEdicion", true);
            vista = "carrera/formulario";
        } catch (RecursoNoEncontradoException e) {
            redirectAttrs.addFlashAttribute("mensajeError", e.getMessage());
            vista = "redirect:/carreras";
        }
        return vista;
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Carrera carrera, RedirectAttributes redirectAttrs) {
        String redireccion = "redirect:/carreras";
        try {
            if (carrera.getIdCarrera() == null) {
                carreraService.guardar(carrera);
                redirectAttrs.addFlashAttribute("mensajeExito", "Carrera creada exitosamente.");
            } else {
                carreraService.actualizar(carrera.getIdCarrera(), carrera);
                redirectAttrs.addFlashAttribute("mensajeExito", "Carrera actualizada exitosamente.");
            }
        } catch (ReglaNegocioException e) {
            redirectAttrs.addFlashAttribute("mensajeError", e.getMessage());
            redireccion = (carrera.getIdCarrera() == null) ? "redirect:/carreras/nueva" : "redirect:/carreras/editar/" + carrera.getIdCarrera();
        }
        return redireccion;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        String redireccion = "redirect:/carreras";
        try {
            carreraService.eliminar(id);
            redirectAttrs.addFlashAttribute("mensajeExito", "Carrera eliminada exitosamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("mensajeError", "No se puede eliminar la carrera porque posee materias o alumnos asociados.");
        }
        return redireccion;
    }
}
