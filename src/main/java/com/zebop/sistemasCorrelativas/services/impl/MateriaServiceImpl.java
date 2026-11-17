package com.zebop.sistemasCorrelativas.services.impl;

import com.zebop.sistemasCorrelativas.models.Correlatividad;
import com.zebop.sistemasCorrelativas.models.Materia;
import com.zebop.sistemasCorrelativas.models.enums.Cuatrimestre;
import com.zebop.sistemasCorrelativas.exception.RecursoNoEncontradoException;
import com.zebop.sistemasCorrelativas.exception.ReglaNegocioException;
import com.zebop.sistemasCorrelativas.repositories.CorrelatividadRepository;
import com.zebop.sistemasCorrelativas.repositories.MateriaRepository;
import com.zebop.sistemasCorrelativas.services.IMateriaService;
import com.zebop.sistemasCorrelativas.util.Constantes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MateriaServiceImpl implements IMateriaService {

    private final MateriaRepository materiaRepository;
    private final CorrelatividadRepository correlatividadRepository;

    public MateriaServiceImpl(MateriaRepository materiaRepository, CorrelatividadRepository correlatividadRepository) {
        this.materiaRepository = materiaRepository;
        this.correlatividadRepository = correlatividadRepository;
    }

    @Override
    public List<Materia> obtenerTodas() {
        List<Materia> lista = new ArrayList<>(materiaRepository.findAll());
        lista.sort(Materia.ORDEN_PLAN);
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
        List<Materia> materias = new ArrayList<>(materiaRepository.findByCarrera_IdCarrera(idCarrera));
        materias.sort(Materia.ORDEN_PLAN);
        return materias;
    }

    @Override
    public Materia guardar(Materia materia) {
        if (materia.getCodigo() != null && materiaRepository.existsByCodigo(materia.getCodigo())) {
            throw new ReglaNegocioException(Constantes.ERROR_CODIGO_MATERIA_DUPLICADO);
        }
        if (materia.getNombre() != null && materia.getCarrera() != null
                && materiaRepository.existsByNombreIgnoreCaseAndCarrera_IdCarrera(materia.getNombre(), materia.getCarrera().getIdCarrera())) {
            throw new ReglaNegocioException(Constantes.ERROR_NOMBRE_MATERIA_DUPLICADO);
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

        Long idCarreraDestino = materiaActualizada.getCarrera() != null
                ? materiaActualizada.getCarrera().getIdCarrera()
                : existente.getCarrera().getIdCarrera();

        if (materiaActualizada.getNombre() != null && !materiaActualizada.getNombre().equalsIgnoreCase(existente.getNombre())) {
            Optional<Materia> conMismoNombre = materiaRepository.findByNombreIgnoreCaseAndCarrera_IdCarrera(materiaActualizada.getNombre(), idCarreraDestino);
            if (conMismoNombre.isPresent() && !conMismoNombre.get().getIdMateria().equals(id)) {
                throw new ReglaNegocioException(Constantes.ERROR_NOMBRE_MATERIA_DUPLICADO);
            }
        }

        if (materiaActualizada.getCargaHoraria() != null && materiaActualizada.getCargaHoraria() < Constantes.CARGA_HORARIA_MINIMA) {
            throw new ReglaNegocioException("La carga horaria debe ser mayor o igual a " + Constantes.CARGA_HORARIA_MINIMA);
        }

        validarPeriodoContraCorrelativas(id, materiaActualizada.getAnioPlan(), materiaActualizada.getCuatrimestre());

        existente.setCodigo(materiaActualizada.getCodigo());
        existente.setNombre(materiaActualizada.getNombre());
        existente.setAnioPlan(materiaActualizada.getAnioPlan());
        existente.setCuatrimestre(materiaActualizada.getCuatrimestre());
        existente.setCargaHoraria(materiaActualizada.getCargaHoraria());
        existente.setCarrera(materiaActualizada.getCarrera());

        Materia resultado = materiaRepository.save(existente);
        return resultado;
    }

    /**
     * Al mover una materia de año/cuatrimestre, sus correlatividades ya cargadas tienen que seguir siendo válidas:
     * sus requisitos deben terminar antes de que empiece, y las materias que habilita deben empezar después de que termine.
     */
    private void validarPeriodoContraCorrelativas(Long idMateria, Integer nuevoAnio, Cuatrimestre nuevoCuatrimestre) {
        List<String> conflictos = new ArrayList<>();

        for (Correlatividad c : correlatividadRepository.findByIdMateriaDestino(idMateria)) {
            Materia requisito = c.getMateriaRequisito();
            if (!Materia.esRequisitoValido(requisito.getAnioPlan(), requisito.getCuatrimestre(), nuevoAnio, nuevoCuatrimestre)) {
                conflictos.add("requisito " + requisito.getCodigo());
            }
        }
        for (Correlatividad c : correlatividadRepository.findByIdMateriaRequisito(idMateria)) {
            Materia destino = c.getMateriaDestino();
            if (!Materia.esRequisitoValido(nuevoAnio, nuevoCuatrimestre, destino.getAnioPlan(), destino.getCuatrimestre())) {
                conflictos.add("habilita a " + destino.getCodigo());
            }
        }

        if (!conflictos.isEmpty()) {
            throw new ReglaNegocioException(String.format(Constantes.ERROR_CAMBIO_PERIODO_ROMPE_CORRELATIVAS, String.join(", ", conflictos)));
        }
    }

    @Override
    public void eliminar(Long id) {
        if (!materiaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(Constantes.ERROR_MATERIA_NO_ENCONTRADA);
        }
        materiaRepository.deleteById(id);
    }
}
