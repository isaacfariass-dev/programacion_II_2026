package com.zebop.sistemasCorrelativas.repositories;

import com.zebop.sistemasCorrelativas.models.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
    Optional<Materia> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    List<Materia> findByCarrera_IdCarrera(Long idCarrera);
    List<Materia> findByCarrera_IdCarreraOrderByAnioPlanAscCodigoAsc(Long idCarrera);
    Optional<Materia> findByNombreIgnoreCaseAndCarrera_IdCarrera(String nombre, Long idCarrera);
    boolean existsByNombreIgnoreCaseAndCarrera_IdCarrera(String nombre, Long idCarrera);
    boolean existsByCarrera_IdCarreraAndAnioPlanGreaterThan(Long idCarrera, Integer anioPlan);
}
