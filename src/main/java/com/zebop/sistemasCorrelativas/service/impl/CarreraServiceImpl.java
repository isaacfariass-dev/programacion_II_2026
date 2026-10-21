package com.zebop.sistemasCorrelativas.service.impl;

import com.zebop.sistemasCorrelativas.entity.Carrera;
import com.zebop.sistemasCorrelativas.exception.RecursoNoEncontradoException;
import com.zebop.sistemasCorrelativas.exception.ReglaNegocioException;
import com.zebop.sistemasCorrelativas.repository.CarreraRepository;
import com.zebop.sistemasCorrelativas.service.ICarreraService;
import com.zebop.sistemasCorrelativas.util.Constantes;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarreraServiceImpl implements ICarreraService {

    private final CarreraRepository carreraRepository;

    public CarreraServiceImpl(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    @Override
    public List<Carrera> obtenerTodas() {
        List<Carrera> lista = carreraRepository.findAll();
        return lista;
    }

    @Override
    public Carrera obtenerPorId(Long id) {
        Carrera carrera = carreraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(Constantes.ERROR_CARRERA_NO_ENCONTRADA));
        return carrera;
    }

    @Override
    public Carrera obtenerPorCodigo(String codigo) {
        Carrera carrera = carreraRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException(Constantes.ERROR_CARRERA_NO_ENCONTRADA));
        return carrera;
    }

    @Override
    public Carrera guardar(Carrera carrera) {
        if (carrera.getCodigo() != null && carreraRepository.existsByCodigo(carrera.getCodigo())) {
            throw new ReglaNegocioException(Constantes.ERROR_CODIGO_CARRERA_DUPLICADO);
        }
        if (carrera.getDuracionAnios() != null && carrera.getDuracionAnios() < Constantes.DURACION_CARRERA_MINIMA_ANIOS) {
            throw new ReglaNegocioException("La duración de la carrera debe ser de al menos " + Constantes.DURACION_CARRERA_MINIMA_ANIOS + " año.");
        }
        Carrera guardada = carreraRepository.save(carrera);
        return guardada;
    }

    @Override
    public Carrera actualizar(Long id, Carrera carreraActualizada) {
        Carrera existente = obtenerPorId(id);

        if (carreraActualizada.getCodigo() != null && !carreraActualizada.getCodigo().equals(existente.getCodigo())) {
            Optional<Carrera> conMismoCodigo = carreraRepository.findByCodigo(carreraActualizada.getCodigo());
            if (conMismoCodigo.isPresent() && !conMismoCodigo.get().getIdCarrera().equals(id)) {
                throw new ReglaNegocioException(Constantes.ERROR_CODIGO_CARRERA_DUPLICADO);
            }
        }

        if (carreraActualizada.getDuracionAnios() != null && carreraActualizada.getDuracionAnios() < Constantes.DURACION_CARRERA_MINIMA_ANIOS) {
            throw new ReglaNegocioException("La duración de la carrera debe ser de al menos " + Constantes.DURACION_CARRERA_MINIMA_ANIOS + " año.");
        }

        existente.setCodigo(carreraActualizada.getCodigo());
        existente.setNombre(carreraActualizada.getNombre());
        existente.setInstitucion(carreraActualizada.getInstitucion());
        existente.setDuracionAnios(carreraActualizada.getDuracionAnios());

        Carrera resultado = carreraRepository.save(existente);
        return resultado;
    }

    @Override
    public void eliminar(Long id) {
        if (!carreraRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(Constantes.ERROR_CARRERA_NO_ENCONTRADA);
        }
        carreraRepository.deleteById(id);
    }
}
