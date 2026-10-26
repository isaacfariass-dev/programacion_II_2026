package com.zebop.sistemasCorrelativas.controllers;

// Capa de Controlador: endpoint REST que recibe la petición HTTP de alta de
// carrera y delega la lógica de negocio en ICarreraService (capa de Servicio).
import com.zebop.sistemasCorrelativas.models.Carrera;
import com.zebop.sistemasCorrelativas.exception.ReglaNegocioException;
import com.zebop.sistemasCorrelativas.services.ICarreraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST para el alta de carreras. Acceso unificado: solo exige sesión
 * activa (validada por AuthInterceptor), sin distinción de roles o permisos.
 */
@RestController
@RequestMapping("/api/carreras")
public class CarreraApiController {

    public record CarreraRequest(String nombre, String codigo, String institucion, Integer duracionAnios) { }

    private final ICarreraService carreraService;

    public CarreraApiController(ICarreraService carreraService) {
        this.carreraService = carreraService;
    }

    @PostMapping(produces = "application/json; charset=UTF-8")
    public ResponseEntity<Carrera> crear(@RequestBody CarreraRequest req) {
        if (esVacio(req.nombre()) || esVacio(req.codigo()) || esVacio(req.institucion())
                || req.duracionAnios() == null || req.duracionAnios() < 1) {
            throw new ReglaNegocioException("nombre, codigo, institucion y duracionAnios (>= 1) son obligatorios.");
        }
        Carrera carrera = new Carrera();
        carrera.setNombre(req.nombre().trim());
        carrera.setCodigo(req.codigo().trim());
        carrera.setInstitucion(req.institucion().trim());
        carrera.setDuracionAnios(req.duracionAnios());
        Carrera creada = carreraService.guardar(carrera);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    private boolean esVacio(String s) {
        return s == null || s.trim().isEmpty();
    }
}
