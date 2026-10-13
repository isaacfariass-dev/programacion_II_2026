package com.zebop.sistemasCorrelativas.repository;

import com.zebop.sistemasCorrelativas.entity.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {
}
