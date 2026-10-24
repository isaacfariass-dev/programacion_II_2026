package com.zebop.sistemasCorrelativas.controller;

import com.zebop.sistemasCorrelativas.dto.PlanEstudioDTO;
import com.zebop.sistemasCorrelativas.dto.SimulacionResultadoDTO;
import com.zebop.sistemasCorrelativas.entity.Alumno;
import com.zebop.sistemasCorrelativas.entity.Materia;
import com.zebop.sistemasCorrelativas.entity.enums.EstadoMateria;
import com.zebop.sistemasCorrelativas.exception.RecursoNoEncontradoException;
import com.zebop.sistemasCorrelativas.exception.ReglaNegocioException;
import com.zebop.sistemasCorrelativas.service.IAcademicoService;
import com.zebop.sistemasCorrelativas.service.IAlumnoService;
import com.zebop.sistemasCorrelativas.service.IMateriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador principal para la visualización del árbol de correlatividades,
 * marcado del historial académico y simulador de impacto.
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

        if (alumnos.isEmpty()) {
            model.addAttribute("mensajeError", "No hay alumnos cargados en la base de datos.");
            vista = "plan/arbol";
        } else {
            Long idElegido = (alumnoId != null) ? alumnoId : alumnos.get(0).getIdAlumno();

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
        String redireccion = "redirect:/plan?alumnoId=" + alumnoId;

        try {
            academicoService.resetearEstadoMateria(alumnoId, idMateria);
            redirectAttrs.addFlashAttribute("mensajeExito", "Se restableció el estado de la materia.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("mensajeError", "Error al restablecer la materia.");
        }

        return redireccion;
    }

    /**
     * Endpoint API para actualización rápida vía AJAX (sin recarga de página).
     * Retorna el plan de estudios actualizado en formato JSON.
     */
    @PostMapping("/api/actualizar-estado")
    @ResponseBody
    public ResponseEntity<?> actualizarEstadoAjax(@RequestParam Long alumnoId,
                                                  @RequestParam Long idMateria,
                                                  @RequestParam EstadoMateria estado,
                                                  @RequestParam(required = false) Double notaFinal) {
        ResponseEntity<?> respuesta;
        try {
            academicoService.actualizarEstadoMateria(alumnoId, idMateria, estado, notaFinal);
            PlanEstudioDTO planActualizado = academicoService.obtenerPlanEstudio(alumnoId);
            respuesta = ResponseEntity.ok(planActualizado);
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            respuesta = ResponseEntity.badRequest().body(errorMap);
        }
        return respuesta;
    }

    /**
     * Endpoint API para resetear una materia vía AJAX (sin recarga de página).
     */
    @PostMapping("/api/resetear-materia")
    @ResponseBody
    public ResponseEntity<?> resetearMateriaAjax(@RequestParam Long alumnoId,
                                                @RequestParam Long idMateria) {
        ResponseEntity<?> respuesta;
        try {
            academicoService.resetearEstadoMateria(alumnoId, idMateria);
            PlanEstudioDTO planActualizado = academicoService.obtenerPlanEstudio(alumnoId);
            respuesta = ResponseEntity.ok(planActualizado);
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            respuesta = ResponseEntity.badRequest().body(errorMap);
        }
        return respuesta;
    }

    /**
     * Muestra la interfaz del simulador "¿Qué pasa si apruebo X materia?".
     */
    @GetMapping("/simulador")
    public String simulador(@RequestParam(required = false) Long alumnoId,
                            @RequestParam(required = false) Long idMateria,
                            Model model) {
        String vista = "plan/simulador";
        List<Alumno> alumnos = alumnoService.obtenerTodos();

        if (!alumnos.isEmpty()) {
            Long idElegido = (alumnoId != null) ? alumnoId : alumnos.get(0).getIdAlumno();
            Alumno alumno = alumnoService.obtenerPorId(idElegido);
            List<Materia> materias = materiaService.obtenerPorCarrera(alumno.getCarrera().getIdCarrera());

            model.addAttribute("alumnos", alumnos);
            model.addAttribute("alumnoActualId", idElegido);
            model.addAttribute("alumno", alumno);
            model.addAttribute("materias", materias);

            if (idMateria != null) {
                SimulacionResultadoDTO simulacion = academicoService.simularAprobacion(idElegido, idMateria);
                model.addAttribute("simulacion", simulacion);
                model.addAttribute("materiaSeleccionadaId", idMateria);
            }
        }

        return vista;
    }
}
