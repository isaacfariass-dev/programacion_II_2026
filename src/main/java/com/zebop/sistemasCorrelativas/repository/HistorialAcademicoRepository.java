package com.zebop.sistemasCorrelativas.repository;

import com.zebop.sistemasCorrelativas.entity.HistorialAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialAcademicoRepository extends JpaRepository<HistorialAcademico, Long> {
}
