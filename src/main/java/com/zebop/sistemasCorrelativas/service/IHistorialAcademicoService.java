package com.zebop.sistemasCorrelativas.service;

import com.zebop.sistemasCorrelativas.entity.HistorialAcademico;
import java.util.List;

public interface IHistorialAcademicoService {

    List<HistorialAcademico> obtenerTodos();

    HistorialAcademico obtenerPorId(Long id);

    List<HistorialAcademico> obtenerPorAlumno(Long idAlumno);

    HistorialAcademico guardar(HistorialAcademico historial);

    HistorialAcademico actualizar(Long id, HistorialAcademico historial);

    void eliminar(Long id);
}
