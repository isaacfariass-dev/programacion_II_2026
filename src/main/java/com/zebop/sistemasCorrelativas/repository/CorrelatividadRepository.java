package com.zebop.sistemasCorrelativas.repository;

import com.zebop.sistemasCorrelativas.entity.Correlatividad;
import com.zebop.sistemasCorrelativas.entity.CorrelatividadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrelatividadRepository extends JpaRepository<Correlatividad, CorrelatividadId> {
    List<Correlatividad> findByIdMateriaDestino(Long idMateriaDestino);
    List<Correlatividad> findByIdMateriaRequisito(Long idMateriaRequisito);
    boolean existsByIdMateriaDestinoAndIdMateriaRequisito(Long idMateriaDestino, Long idMateriaRequisito);
    void deleteByIdMateriaDestinoAndIdMateriaRequisito(Long idMateriaDestino, Long idMateriaRequisito);
}
