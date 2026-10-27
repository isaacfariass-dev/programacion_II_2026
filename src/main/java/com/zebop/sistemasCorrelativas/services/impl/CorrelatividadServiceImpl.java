package com.zebop.sistemasCorrelativas.services.impl;

import com.zebop.sistemasCorrelativas.models.Correlatividad;
import com.zebop.sistemasCorrelativas.models.CorrelatividadId;
import com.zebop.sistemasCorrelativas.exception.CorrelatividadCiclicaException;
import com.zebop.sistemasCorrelativas.exception.RecursoNoEncontradoException;
import com.zebop.sistemasCorrelativas.exception.ReglaNegocioException;
import com.zebop.sistemasCorrelativas.repositories.CorrelatividadRepository;
import com.zebop.sistemasCorrelativas.services.ICorrelatividadService;
import com.zebop.sistemasCorrelativas.util.Constantes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

@Service
public class CorrelatividadServiceImpl implements ICorrelatividadService {

    private final CorrelatividadRepository correlatividadRepository;

    public CorrelatividadServiceImpl(CorrelatividadRepository correlatividadRepository) {
        this.correlatividadRepository = correlatividadRepository;
    }

    @Override
    public List<Correlatividad> obtenerTodas() {
        List<Correlatividad> lista = correlatividadRepository.findAll();
        return lista;
    }

    @Override
    public List<Correlatividad> obtenerRequisitosDeMateria(Long idMateriaDestino) {
        List<Correlatividad> lista = correlatividadRepository.findByIdMateriaDestino(idMateriaDestino);
        return lista;
    }

    @Override
    public List<Correlatividad> obtenerMateriasQueHabilita(Long idMateriaRequisito) {
        List<Correlatividad> lista = correlatividadRepository.findByIdMateriaRequisito(idMateriaRequisito);
        return lista;
    }

    @Override
    public Correlatividad guardar(Correlatividad correlatividad) {
        Long destinoId = correlatividad.getIdMateriaDestino();
        Long requisitoId = correlatividad.getIdMateriaRequisito();

        if (Objects.equals(destinoId, requisitoId)) {
            throw new ReglaNegocioException(Constantes.ERROR_AUTODEPENDENCIA);
        }

        Long carreraDestinoId = correlatividad.getMateriaDestino().getCarrera().getIdCarrera();
        Long carreraRequisitoId = correlatividad.getMateriaRequisito().getCarrera().getIdCarrera();
        if (!Objects.equals(carreraDestinoId, carreraRequisitoId)) {
            throw new ReglaNegocioException(Constantes.ERROR_CARRERAS_DISTINTAS);
        }

        if (correlatividadRepository.existsByIdMateriaDestinoAndIdMateriaRequisito(destinoId, requisitoId)) {
            throw new ReglaNegocioException(Constantes.ERROR_CORRELATIVIDAD_DUPLICADA);
        }

        if (hayCiclo(destinoId, requisitoId)) {
            throw new CorrelatividadCiclicaException(Constantes.ERROR_CICLO_CORRELATIVIDAD);
        }

        Correlatividad guardada = correlatividadRepository.save(correlatividad);
        return guardada;
    }

    @Override
    @Transactional
    public void eliminar(Long idMateriaDestino, Long idMateriaRequisito) {
        CorrelatividadId id = new CorrelatividadId(idMateriaDestino, idMateriaRequisito);
        if (!correlatividadRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(Constantes.ERROR_CORRELATIVIDAD_NO_ENCONTRADA);
        }
        correlatividadRepository.deleteById(id);
    }

    /**
     * Verifica si agregar destino -> requisito generaría un ciclo.
     * Hay ciclo si ya existe un camino desde requisito hasta destino.
     */
    private boolean hayCiclo(Long idMateriaDestino, Long idMateriaRequisito) {
        boolean cicloDetectado = false;
        Set<Long> visitados = new HashSet<>();
        Queue<Long> cola = new ArrayDeque<>();

        cola.add(idMateriaRequisito);
        visitados.add(idMateriaRequisito);

        while (!cola.isEmpty() && !cicloDetectado) {
            Long actual = cola.poll();
            if (actual.equals(idMateriaDestino)) {
                cicloDetectado = true;
            } else {
                List<Correlatividad> dependencias = correlatividadRepository.findByIdMateriaDestino(actual);
                for (Correlatividad dep : dependencias) {
                    Long siguiente = dep.getIdMateriaRequisito();
                    if (!visitados.contains(siguiente)) {
                        visitados.add(siguiente);
                        cola.add(siguiente);
                    }
                }
            }
        }

        return cicloDetectado;
    }
}
