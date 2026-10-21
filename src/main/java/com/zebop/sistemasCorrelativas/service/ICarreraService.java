package com.zebop.sistemasCorrelativas.service;

import com.zebop.sistemasCorrelativas.entity.Carrera;
import java.util.List;

public interface ICarreraService {

    List<Carrera> obtenerTodas();

    Carrera obtenerPorId(Long id);

    Carrera obtenerPorCodigo(String codigo);

    Carrera guardar(Carrera carrera);

    Carrera actualizar(Long id, Carrera carrera);

    void eliminar(Long id);
}
