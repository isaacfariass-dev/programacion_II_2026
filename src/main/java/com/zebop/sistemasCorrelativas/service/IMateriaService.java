package com.zebop.sistemasCorrelativas.service;

import com.zebop.sistemasCorrelativas.entity.Materia;
import java.util.List;

public interface IMateriaService {

    List<Materia> obtenerTodas();

    Materia obtenerPorId(Long id);

    Materia obtenerPorCodigo(String codigo);

    List<Materia> obtenerPorCarrera(Long idCarrera);

    Materia guardar(Materia materia);

    Materia actualizar(Long id, Materia materia);

    void eliminar(Long id);
}
