package com.zebop.sistemasCorrelativas.repository;

import com.zebop.sistemasCorrelativas.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
}
