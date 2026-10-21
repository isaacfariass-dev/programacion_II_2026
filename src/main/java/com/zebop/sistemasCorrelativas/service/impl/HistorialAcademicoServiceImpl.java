package com.zebop.sistemasCorrelativas.service.impl;

import com.zebop.sistemasCorrelativas.entity.HistorialAcademico;
import com.zebop.sistemasCorrelativas.exception.RecursoNoEncontradoException;
import com.zebop.sistemasCorrelativas.exception.ReglaNegocioException;
import com.zebop.sistemasCorrelativas.repository.HistorialAcademicoRepository;
import com.zebop.sistemasCorrelativas.service.IHistorialAcademicoService;
import com.zebop.sistemasCorrelativas.util.Constantes;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistorialAcademicoServiceImpl implements IHistorialAcademicoService {

    private final HistorialAcademicoRepository historialRepository;

    public HistorialAcademicoServiceImpl(HistorialAcademicoRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    @Override
    public List<HistorialAcademico> obtenerTodos() {
        List<HistorialAcademico> lista = historialRepository.findAll();
        return lista;
    }

    @Override
    public HistorialAcademico obtenerPorId(Long id) {
        HistorialAcademico historial = historialRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(Constantes.ERROR_HISTORIAL_NO_ENCONTRADO));
        return historial;
    }

    @Override
    public List<HistorialAcademico> obtenerPorAlumno(Long idAlumno) {
        List<HistorialAcademico> lista = historialRepository.findByAlumno_IdAlumno(idAlumno);
        return lista;
    }

    @Override
    public HistorialAcademico guardar(HistorialAcademico historial) {
        validarNota(historial.getNotaFinal());

        if (historial.getAlumno() != null && historial.getMateria() != null) {
            Optional<HistorialAcademico> existente = historialRepository.findByAlumno_IdAlumnoAndMateria_IdMateria(
                    historial.getAlumno().getIdAlumno(),
                    historial.getMateria().getIdMateria()
            );
            if (existente.isPresent()) {
                throw new ReglaNegocioException("Ya existe un registro en el historial para este alumno y materia.");
            }
        }

        HistorialAcademico guardado = historialRepository.save(historial);
        return guardado;
    }

    @Override
    public HistorialAcademico actualizar(Long id, HistorialAcademico historialActualizado) {
        HistorialAcademico existente = obtenerPorId(id);
        validarNota(historialActualizado.getNotaFinal());

        existente.setEstado(historialActualizado.getEstado());
        existente.setNotaFinal(historialActualizado.getNotaFinal());

        HistorialAcademico resultado = historialRepository.save(existente);
        return resultado;
    }

    @Override
    public void eliminar(Long id) {
        if (!historialRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(Constantes.ERROR_HISTORIAL_NO_ENCONTRADO);
        }
        historialRepository.deleteById(id);
    }

    private void validarNota(Double nota) {
        if (nota != null && (nota < Constantes.NOTA_MINIMA || nota > Constantes.NOTA_MAXIMA)) {
            throw new ReglaNegocioException("La nota debe estar comprendida entre " + Constantes.NOTA_MINIMA + " y " + Constantes.NOTA_MAXIMA);
        }
    }
}
