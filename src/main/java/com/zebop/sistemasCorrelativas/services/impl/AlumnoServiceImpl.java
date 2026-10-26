package com.zebop.sistemasCorrelativas.services.impl;

import com.zebop.sistemasCorrelativas.models.Alumno;
import com.zebop.sistemasCorrelativas.exception.RecursoNoEncontradoException;
import com.zebop.sistemasCorrelativas.exception.ReglaNegocioException;
import com.zebop.sistemasCorrelativas.repositories.AlumnoRepository;
import com.zebop.sistemasCorrelativas.services.IAlumnoService;
import com.zebop.sistemasCorrelativas.util.Constantes;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlumnoServiceImpl implements IAlumnoService {

    private final AlumnoRepository alumnoRepository;

    public AlumnoServiceImpl(AlumnoRepository alumnoRepository) {
        this.alumnoRepository = alumnoRepository;
    }

    @Override
    public List<Alumno> obtenerTodos() {
        List<Alumno> lista = alumnoRepository.findAll();
        return lista;
    }

    @Override
    public Alumno obtenerPorId(Long id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(Constantes.ERROR_ALUMNO_NO_ENCONTRADA));
        return alumno;
    }

    @Override
    public Alumno obtenerPorLegajo(String legajo) {
        Alumno alumno = alumnoRepository.findByLegajo(legajo)
                .orElseThrow(() -> new RecursoNoEncontradoException(Constantes.ERROR_ALUMNO_NO_ENCONTRADA));
        return alumno;
    }

    @Override
    public Alumno guardar(Alumno alumno) {
        if (alumno.getLegajo() != null && alumnoRepository.existsByLegajo(alumno.getLegajo())) {
            throw new ReglaNegocioException(Constantes.ERROR_LEGAJO_DUPLICADO);
        }
        Alumno guardado = alumnoRepository.save(alumno);
        return guardado;
    }

    @Override
    public Alumno actualizar(Long id, Alumno alumnoActualizado) {
        Alumno existente = obtenerPorId(id);

        if (alumnoActualizado.getLegajo() != null && !alumnoActualizado.getLegajo().equals(existente.getLegajo())) {
            Optional<Alumno> conMismoLegajo = alumnoRepository.findByLegajo(alumnoActualizado.getLegajo());
            if (conMismoLegajo.isPresent() && !conMismoLegajo.get().getIdAlumno().equals(id)) {
                throw new ReglaNegocioException(Constantes.ERROR_LEGAJO_DUPLICADO);
            }
        }

        existente.setNombre(alumnoActualizado.getNombre());
        existente.setApellido(alumnoActualizado.getApellido());
        existente.setLegajo(alumnoActualizado.getLegajo());
        existente.setAnioIngreso(alumnoActualizado.getAnioIngreso());
        existente.setCarrera(alumnoActualizado.getCarrera());

        Alumno resultado = alumnoRepository.save(existente);
        return resultado;
    }

    @Override
    public void eliminar(Long id) {
        if (!alumnoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(Constantes.ERROR_ALUMNO_NO_ENCONTRADA);
        }
        alumnoRepository.deleteById(id);
    }
}
