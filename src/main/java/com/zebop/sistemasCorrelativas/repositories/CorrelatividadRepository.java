package com.zebop.sistemasCorrelativas.repositories;

import com.zebop.sistemasCorrelativas.models.Correlatividad;
import com.zebop.sistemasCorrelativas.models.CorrelatividadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrelatividadRepository extends JpaRepository<Correlatividad, CorrelatividadId> {
    List<Correlatividad> findByIdMateriaDestino(Long idMateriaDestino);
    List<Correlatividad> findByIdMateriaRequisito(Long idMateriaRequisito);
    boolean existsByIdMateriaDestinoAndIdMateriaRequisito(Long idMateriaDestino, Long idMateriaRequisito);
}
