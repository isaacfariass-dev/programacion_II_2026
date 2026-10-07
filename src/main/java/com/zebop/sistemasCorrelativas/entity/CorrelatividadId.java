package com.zebop.sistemasCorrelativas.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clave primaria compuesta para la entidad Correlatividad.
 * Mapea las columnas (id_materia_destino, id_materia_requisito).
 */
public class CorrelatividadId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idMateriaDestino;
    private Long idMateriaRequisito;

    public CorrelatividadId() {
    }

    public CorrelatividadId(Long idMateriaDestino, Long idMateriaRequisito) {
        this.idMateriaDestino = idMateriaDestino;
        this.idMateriaRequisito = idMateriaRequisito;
    }

    public Long getIdMateriaDestino() {
        return idMateriaDestino;
    }

    public void setIdMateriaDestino(Long idMateriaDestino) {
        this.idMateriaDestino = idMateriaDestino;
    }

    public Long getIdMateriaRequisito() {
        return idMateriaRequisito;
    }

    public void setIdMateriaRequisito(Long idMateriaRequisito) {
        this.idMateriaRequisito = idMateriaRequisito;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CorrelatividadId that = (CorrelatividadId) o;
        return Objects.equals(idMateriaDestino, that.idMateriaDestino) &&
               Objects.equals(idMateriaRequisito, that.idMateriaRequisito);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMateriaDestino, idMateriaRequisito);
    }
}
