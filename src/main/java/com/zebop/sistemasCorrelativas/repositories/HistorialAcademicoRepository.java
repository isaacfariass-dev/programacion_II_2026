package com.zebop.sistemasCorrelativas.repositories;

import com.zebop.sistemasCorrelativas.models.HistorialAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistorialAcademicoRepository extends JpaRepository<HistorialAcademico, Long> {
    List<HistorialAcademico> findByAlumno_IdAlumno(Long idAlumno);
    Optional<HistorialAcademico> findByAlumno_IdAlumnoAndMateria_IdMateria(Long idAlumno, Long idMateria);
}
