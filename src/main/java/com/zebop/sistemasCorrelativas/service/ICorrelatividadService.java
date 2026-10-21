package com.zebop.sistemasCorrelativas.service;

import com.zebop.sistemasCorrelativas.entity.Correlatividad;
import java.util.List;

public interface ICorrelatividadService {

    List<Correlatividad> obtenerTodas();

    List<Correlatividad> obtenerRequisitosDeMateria(Long idMateriaDestino);

    List<Correlatividad> obtenerMateriasQueHabilita(Long idMateriaRequisito);

    Correlatividad guardar(Correlatividad correlatividad);

    void eliminar(Long idMateriaDestino, Long idMateriaRequisito);
}
