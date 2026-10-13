package com.zebop.sistemasCorrelativas.repository;

import com.zebop.sistemasCorrelativas.entity.Correlatividad;
import com.zebop.sistemasCorrelativas.entity.CorrelatividadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorrelatividadRepository extends JpaRepository<Correlatividad, CorrelatividadId> {
}
