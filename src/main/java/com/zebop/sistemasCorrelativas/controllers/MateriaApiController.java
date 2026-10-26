package com.zebop.sistemasCorrelativas.controllers;

// Capa de Controlador: endpoint REST que recibe la petición HTTP de alta de
// materia y delega la lógica de negocio en IMateriaService (capa de Servicio).
import com.zebop.sistemasCorrelativas.models.Materia;
import com.zebop.sistemasCorrelativas.models.enums.Cuatrimestre;
import com.zebop.sistemasCorrelativas.exception.ReglaNegocioException;
import com.zebop.sistemasCorrelativas.services.ICarreraService;
import com.zebop.sistemasCorrelativas.services.IMateriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST para el alta de materias vinculadas a una carrera. Acceso unificado:
 * solo exige sesión activa (validada por AuthInterceptor), sin roles ni permisos.
 */
@RestController
@RequestMapping("/api/materias")
public class MateriaApiController {

    public record MateriaRequest(String nombre, String codigo, Long carreraId, Integer anioPlan,
                                 Cuatrimestre cuatrimestre, Integer cargaHoraria) { }

    private final IMateriaService materiaService;
    private final ICarreraService carreraService;

    public MateriaApiController(IMateriaService materiaService, ICarreraService carreraService) {
        this.materiaService = materiaService;
        this.carreraService = carreraService;
    }

    @PostMapping(produces = "application/json; charset=UTF-8")
    public ResponseEntity<Materia> crear(@RequestBody MateriaRequest req) {
        if (req.nombre() == null || req.nombre().isBlank() || req.codigo() == null || req.codigo().isBlank()
                || req.carreraId() == null || req.anioPlan() == null || req.anioPlan() < 1
                || req.cuatrimestre() == null || req.cargaHoraria() == null) {
            throw new ReglaNegocioException(
                    "nombre, codigo, carreraId, anioPlan, cuatrimestre y cargaHoraria son obligatorios.");
        }
        Materia materia = new Materia();
        materia.setNombre(req.nombre().trim());
        materia.setCodigo(req.codigo().trim());
        materia.setCarrera(carreraService.obtenerPorId(req.carreraId()));
        materia.setAnioPlan(req.anioPlan());
        materia.setCuatrimestre(req.cuatrimestre());
        materia.setCargaHoraria(req.cargaHoraria());
        Materia creada = materiaService.guardar(materia);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }
}
