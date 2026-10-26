package com.zebop.sistemasCorrelativas.services;

import com.zebop.sistemasCorrelativas.models.Correlatividad;
import java.util.List;

public interface ICorrelatividadService {

    List<Correlatividad> obtenerTodas();

    List<Correlatividad> obtenerRequisitosDeMateria(Long idMateriaDestino);

    List<Correlatividad> obtenerMateriasQueHabilita(Long idMateriaRequisito);

    Correlatividad guardar(Correlatividad correlatividad);

    void eliminar(Long idMateriaDestino, Long idMateriaRequisito);
}
