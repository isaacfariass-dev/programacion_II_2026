package com.zebop.sistemasCorrelativas.services;

import com.zebop.sistemasCorrelativas.models.HistorialAcademico;
import java.util.List;

public interface IHistorialAcademicoService {

    List<HistorialAcademico> obtenerTodos();

    HistorialAcademico obtenerPorId(Long id);

    List<HistorialAcademico> obtenerPorAlumno(Long idAlumno);

    HistorialAcademico guardar(HistorialAcademico historial);

    HistorialAcademico actualizar(Long id, HistorialAcademico historial);

    void eliminar(Long id);
}
