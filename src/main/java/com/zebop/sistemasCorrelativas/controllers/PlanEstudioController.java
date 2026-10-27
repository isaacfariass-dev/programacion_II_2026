package com.zebop.sistemasCorrelativas.controllers;

import com.zebop.sistemasCorrelativas.dto.PlanEstudioDTO;
import com.zebop.sistemasCorrelativas.models.Alumno;
import com.zebop.sistemasCorrelativas.models.Materia;
import com.zebop.sistemasCorrelativas.models.enums.EstadoMateria;
import com.zebop.sistemasCorrelativas.exception.RecursoNoEncontradoException;
import com.zebop.sistemasCorrelativas.exception.ReglaNegocioException;
import com.zebop.sistemasCorrelativas.services.IAcademicoService;
import com.zebop.sistemasCorrelativas.services.IAlumnoService;
import com.zebop.sistemasCorrelativas.services.IMateriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador principal para la visualización del árbol de correlatividades
 * y el marcado del historial académico.
 */
@Controller
@RequestMapping("/plan")
public class PlanEstudioController {

    private final IAcademicoService academicoService;
    private final IAlumnoService alumnoService;
    private final IMateriaService materiaService;

    public PlanEstudioController(IAcademicoService academicoService,
                                 IAlumnoService alumnoService,
                                 IMateriaService materiaService) {
        this.academicoService = academicoService;
        this.alumnoService = alumnoService;
        this.materiaService = materiaService;
    }

    /**
     * Muestra el plan de estudios del alumno con las materias agrupadas por año
     * y coloreadas según su estado de correlatividad.
     */
    @GetMapping
    public String verPlan(@RequestParam(required = false) Long alumnoId, Model model) {
        String vista;
        List<Alumno> alumnos = alumnoService.obtenerTodos();

        Long idElegido = alumnoId;

        if (alumnos.isEmpty()) {
            model.addAttribute("mensajeError", "No hay alumnos cargados en la base de datos.");
            vista = "plan/arbol";
        } else {
            if (idElegido == null) {
                idElegido = alumnos.get(0).getIdAlumno();
            }

            try {
                PlanEstudioDTO plan = academicoService.obtenerPlanEstudio(idElegido);
                model.addAttribute("plan", plan);
                model.addAttribute("alumnos", alumnos);
                model.addAttribute("alumnoActualId", idElegido);
                model.addAttribute("estadosMateria", EstadoMateria.values());
                vista = "plan/arbol";
            } catch (RecursoNoEncontradoException e) {
                model.addAttribute("mensajeError", e.getMessage());
                vista = "plan/arbol";
            }
        }

        return vista;
    }

    /**
     * Actualiza el estado de una materia (Aprobada, Regular, Desaprobada) para el alumno.
     */
    @PostMapping("/actualizar-estado")
    public String actualizarEstado(@RequestParam Long alumnoId,
                                   @RequestParam Long idMateria,
                                   @RequestParam EstadoMateria estado,
                                   @RequestParam(required = false) Double notaFinal,
                                   RedirectAttributes redirectAttrs) {
        String redireccion = "redirect:/plan?alumnoId=" + alumnoId;

        try {
            academicoService.actualizarEstadoMateria(alumnoId, idMateria, estado, notaFinal);
            redirectAttrs.addFlashAttribute("mensajeExito", "Estado académico actualizado correctamente.");
        } catch (ReglaNegocioException | RecursoNoEncontradoException e) {
            redirectAttrs.addFlashAttribute("mensajeError", e.getMessage());
        }

        return redireccion;
    }

    /**
     * Elimina el registro del historial académico para esa materia, reseteándola a no cursada.
     */
    @PostMapping("/resetear-materia")
    public String resetearMateria(@RequestParam Long alumnoId,
                                  @RequestParam Long idMateria,
                                  RedirectAttributes redirectAttrs) {
        academicoService.resetearEstadoMateria(alumnoId, idMateria);
        redirectAttrs.addFlashAttribute("mensajeExito", "Se restableció el estado de la materia.");
        return "redirect:/plan?alumnoId=" + alumnoId;
    }
}
