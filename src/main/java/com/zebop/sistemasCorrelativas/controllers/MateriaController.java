package com.zebop.sistemasCorrelativas.controllers;

import com.zebop.sistemasCorrelativas.models.Carrera;
import com.zebop.sistemasCorrelativas.models.Correlatividad;
import com.zebop.sistemasCorrelativas.models.Materia;
import com.zebop.sistemasCorrelativas.models.enums.Cuatrimestre;
import com.zebop.sistemasCorrelativas.models.enums.TipoRequisito;
import com.zebop.sistemasCorrelativas.exception.CorrelatividadCiclicaException;
import com.zebop.sistemasCorrelativas.exception.RecursoNoEncontradoException;
import com.zebop.sistemasCorrelativas.exception.ReglaNegocioException;
import com.zebop.sistemasCorrelativas.services.ICarreraService;
import com.zebop.sistemasCorrelativas.services.ICorrelatividadService;
import com.zebop.sistemasCorrelativas.services.IMateriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la gestión y ABM de Materias y sus Correlatividades.
 */
@Controller
@RequestMapping("/materias")
public class MateriaController {

    private final IMateriaService materiaService;
    private final ICarreraService carreraService;
    private final ICorrelatividadService correlatividadService;

    public MateriaController(IMateriaService materiaService,
                             ICarreraService carreraService,
                             ICorrelatividadService correlatividadService) {
        this.materiaService = materiaService;
        this.carreraService = carreraService;
        this.correlatividadService = correlatividadService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Long carreraId, Model model) {
        List<Carrera> carreras = carreraService.obtenerTodas();
        List<Materia> materias;

        if (carreraId != null) {
            materias = materiaService.obtenerPorCarrera(carreraId);
        } else {
            materias = materiaService.obtenerTodas();
        }

        model.addAttribute("carreras", carreras);
        model.addAttribute("materias", materias);
        model.addAttribute("carreraSeleccionadaId", carreraId);
        return "materia/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("materia", new Materia());
        model.addAttribute("carreras", carreraService.obtenerTodas());
        model.addAttribute("cuatrimestres", Cuatrimestre.values());
        model.addAttribute("esEdicion", false);
        return "materia/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        String vista;
        try {
            Materia materia = materiaService.obtenerPorId(id);
            model.addAttribute("materia", materia);
            model.addAttribute("carreras", carreraService.obtenerTodas());
            model.addAttribute("cuatrimestres", Cuatrimestre.values());
            model.addAttribute("esEdicion", true);
            vista = "materia/formulario";
        } catch (RecursoNoEncontradoException e) {
            redirectAttrs.addFlashAttribute("mensajeError", e.getMessage());
            vista = "redirect:/materias";
        }
        return vista;
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Materia materia,
                          @RequestParam Long carreraId,
                          RedirectAttributes redirectAttrs) {
        String redireccion = "redirect:/materias";
        try {
            Carrera carrera = carreraService.obtenerPorId(carreraId);
            materia.setCarrera(carrera);

            if (materia.getIdMateria() == null) {
                materiaService.guardar(materia);
                redirectAttrs.addFlashAttribute("mensajeExito", "Materia creada exitosamente.");
            } else {
                materiaService.actualizar(materia.getIdMateria(), materia);
                redirectAttrs.addFlashAttribute("mensajeExito", "Materia actualizada exitosamente.");
            }
        } catch (ReglaNegocioException e) {
            redirectAttrs.addFlashAttribute("mensajeError", e.getMessage());
            redireccion = (materia.getIdMateria() == null) ? "redirect:/materias/nueva" : "redirect:/materias/editar/" + materia.getIdMateria();
        }
        return redireccion;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        String redireccion = "redirect:/materias";
        try {
            materiaService.eliminar(id);
            redirectAttrs.addFlashAttribute("mensajeExito", "Materia eliminada exitosamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("mensajeError", "No se puede eliminar la materia porque tiene correlatividades o historiales académicos asociados.");
        }
        return redireccion;
    }

    @GetMapping("/{id}/correlatividades")
    public String gestionarCorrelatividades(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        String vista;
        try {
            Materia materia = materiaService.obtenerPorId(id);
            List<Correlatividad> requisitos = correlatividadService.obtenerRequisitosDeMateria(id);
            List<Correlatividad> materiasQueHabilita = correlatividadService.obtenerMateriasQueHabilita(id);

            List<Materia> materiasDeCarrera = materiaService.obtenerPorCarrera(materia.getCarrera().getIdCarrera());
            List<Materia> candidatas = new ArrayList<>();
            for (Materia m : materiasDeCarrera) {
                if (!m.getIdMateria().equals(id)) {
                    candidatas.add(m);
                }
            }

            model.addAttribute("materia", materia);
            model.addAttribute("requisitos", requisitos);
            model.addAttribute("materiasQueHabilita", materiasQueHabilita);
            model.addAttribute("candidatas", candidatas);
            model.addAttribute("tiposRequisito", TipoRequisito.values());
            vista = "materia/correlatividades";
        } catch (RecursoNoEncontradoException e) {
            redirectAttrs.addFlashAttribute("mensajeError", e.getMessage());
            vista = "redirect:/materias";
        }
        return vista;
    }

    @PostMapping("/{id}/correlatividades/agregar")
    public String agregarCorrelatividad(@PathVariable Long id,
                                        @RequestParam Long idMateriaRequisito,
                                        @RequestParam TipoRequisito tipoRequisito,
                                        RedirectAttributes redirectAttrs) {
        String redireccion = "redirect:/materias/" + id + "/correlatividades";
        try {
            Materia destino = materiaService.obtenerPorId(id);
            Materia requisito = materiaService.obtenerPorId(idMateriaRequisito);

            Correlatividad correlatividad = new Correlatividad(destino, requisito, tipoRequisito);
            correlatividadService.guardar(correlatividad);

            redirectAttrs.addFlashAttribute("mensajeExito", "Correlatividad agregada correctamente.");
        } catch (ReglaNegocioException e) {
            redirectAttrs.addFlashAttribute("mensajeError", e.getMessage());
        }
        return redireccion;
    }

    @PostMapping("/{id}/correlatividades/eliminar")
    public String eliminarCorrelatividad(@PathVariable Long id,
                                         @RequestParam Long idMateriaRequisito,
                                         RedirectAttributes redirectAttrs) {
        String redireccion = "redirect:/materias/" + id + "/correlatividades";
        try {
            correlatividadService.eliminar(id, idMateriaRequisito);
            redirectAttrs.addFlashAttribute("mensajeExito", "Correlatividad eliminada correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("mensajeError", "Error al eliminar la correlatividad.");
        }
        return redireccion;
    }
}
