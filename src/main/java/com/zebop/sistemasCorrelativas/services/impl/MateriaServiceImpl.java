package com.zebop.sistemasCorrelativas.services.impl;

import com.zebop.sistemasCorrelativas.models.Materia;
import com.zebop.sistemasCorrelativas.exception.RecursoNoEncontradoException;
import com.zebop.sistemasCorrelativas.exception.ReglaNegocioException;
import com.zebop.sistemasCorrelativas.repositories.MateriaRepository;
import com.zebop.sistemasCorrelativas.services.IMateriaService;
import com.zebop.sistemasCorrelativas.util.Constantes;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MateriaServiceImpl implements IMateriaService {

    private final MateriaRepository materiaRepository;

    public MateriaServiceImpl(MateriaRepository materiaRepository) {
        this.materiaRepository = materiaRepository;
    }

    @Override
    public List<Materia> obtenerTodas() {
        List<Materia> lista = materiaRepository.findAll();
        return lista;
    }

    @Override
    public Materia obtenerPorId(Long id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(Constantes.ERROR_MATERIA_NO_ENCONTRADA));
        return materia;
    }

    @Override
    public Materia obtenerPorCodigo(String codigo) {
        Materia materia = materiaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException(Constantes.ERROR_MATERIA_NO_ENCONTRADA));
        return materia;
    }

    @Override
    public List<Materia> obtenerPorCarrera(Long idCarrera) {
        List<Materia> materias = materiaRepository.findByCarrera_IdCarrera(idCarrera);
        return materias;
    }

    @Override
    public Materia guardar(Materia materia) {
        if (materia.getCodigo() != null && materiaRepository.existsByCodigo(materia.getCodigo())) {
            throw new ReglaNegocioException(Constantes.ERROR_CODIGO_MATERIA_DUPLICADO);
        }
        if (materia.getCargaHoraria() != null && materia.getCargaHoraria() < Constantes.CARGA_HORARIA_MINIMA) {
            throw new ReglaNegocioException("La carga horaria debe ser mayor o igual a " + Constantes.CARGA_HORARIA_MINIMA);
        }
        Materia guardada = materiaRepository.save(materia);
        return guardada;
    }

    @Override
    public Materia actualizar(Long id, Materia materiaActualizada) {
        Materia existente = obtenerPorId(id);

        if (materiaActualizada.getCodigo() != null && !materiaActualizada.getCodigo().equals(existente.getCodigo())) {
            Optional<Materia> conMismoCodigo = materiaRepository.findByCodigo(materiaActualizada.getCodigo());
            if (conMismoCodigo.isPresent() && !conMismoCodigo.get().getIdMateria().equals(id)) {
                throw new ReglaNegocioException(Constantes.ERROR_CODIGO_MATERIA_DUPLICADO);
            }
        }

        if (materiaActualizada.getCargaHoraria() != null && materiaActualizada.getCargaHoraria() < Constantes.CARGA_HORARIA_MINIMA) {
            throw new ReglaNegocioException("La carga horaria debe ser mayor o igual a " + Constantes.CARGA_HORARIA_MINIMA);
        }

        existente.setCodigo(materiaActualizada.getCodigo());
        existente.setNombre(materiaActualizada.getNombre());
        existente.setAnioPlan(materiaActualizada.getAnioPlan());
        existente.setCuatrimestre(materiaActualizada.getCuatrimestre());
        existente.setCargaHoraria(materiaActualizada.getCargaHoraria());
        existente.setCarrera(materiaActualizada.getCarrera());

        Materia resultado = materiaRepository.save(existente);
        return resultado;
    }

    @Override
    public void eliminar(Long id) {
        if (!materiaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(Constantes.ERROR_MATERIA_NO_ENCONTRADA);
        }
        materiaRepository.deleteById(id);
    }
}
