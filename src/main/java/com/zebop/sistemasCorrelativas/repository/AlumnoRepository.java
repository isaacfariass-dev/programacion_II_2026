package com.zebop.sistemasCorrelativas.repository;
import com.zebop.sistemasCorrelativas.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
}
