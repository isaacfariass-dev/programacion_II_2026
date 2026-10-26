package com.zebop.sistemasCorrelativas.models;

import java.io.Serializable;
import java.util.Objects;
import lombok.*;
/**
 * Clave primaria compuesta para la entidad Correlatividad.
 * Mapea las columnas (id_materia_destino, id_materia_requisito).
 */
@Getter
@NoArgsConstructor 
@AllArgsConstructor
public class CorrelatividadId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idMateriaDestino;
    private Long idMateriaRequisito;

    public void setIdMateriaDestino(Long idMateriaDestino) {
        this.idMateriaDestino = idMateriaDestino;
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
