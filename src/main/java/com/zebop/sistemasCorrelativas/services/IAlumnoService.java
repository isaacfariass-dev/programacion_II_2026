package com.zebop.sistemasCorrelativas.services;

import com.zebop.sistemasCorrelativas.models.Alumno;
import java.util.List;

public interface IAlumnoService {

    List<Alumno> obtenerTodos();

    Alumno obtenerPorId(Long id);

    Alumno obtenerPorLegajo(String legajo);

    Alumno guardar(Alumno alumno);

    Alumno actualizar(Long id, Alumno alumno);

    void eliminar(Long id);
}
