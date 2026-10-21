package com.zebop.sistemasCorrelativas.repository;
import com.zebop.sistemasCorrelativas.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    Optional<Alumno> findByLegajo(String legajo);
    boolean existsByLegajo(String legajo);
}
