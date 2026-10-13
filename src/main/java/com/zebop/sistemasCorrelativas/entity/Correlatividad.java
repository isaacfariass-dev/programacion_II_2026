package com.zebop.sistemasCorrelativas.entity;

import com.zebop.sistemasCorrelativas.entity.enums.TipoRequisito;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import lombok.*;
/**
 * Entidad que modela la relación N:M recursiva de correlatividad entre materias.
 * Mapea la tabla 'correlatividad' con clave compuesta.
 */
@Entity
@Getter
@NoArgsConstructor 
@AllArgsConstructor
@ToString
@Table(name = "correlatividad")
@IdClass(CorrelatividadId.class)
public class Correlatividad implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_materia_destino")
    private Long idMateriaDestino;

    @Id
    @Column(name = "id_materia_requisito")
    private Long idMateriaRequisito;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_materia_destino", insertable = false, updatable = false)
    private Materia materiaDestino;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_materia_requisito", insertable = false, updatable = false)
    private Materia materiaRequisito;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_requisito", nullable = false)
    private TipoRequisito tipoRequisito;


    public Correlatividad(Materia materiaDestino, Materia materiaRequisito, TipoRequisito tipoRequisito) {
        this.materiaDestino = materiaDestino;
        this.materiaRequisito = materiaRequisito;
        if (materiaDestino != null) {
            this.idMateriaDestino = materiaDestino.getIdMateria();
        }
        if (materiaRequisito != null) {
            this.idMateriaRequisito = materiaRequisito.getIdMateria();
        }
        this.tipoRequisito = tipoRequisito;
    }

    public void setIdMateriaDestino(Long idMateriaDestino) {
        this.idMateriaDestino = idMateriaDestino;
    }
    public void setIdMateriaRequisito(Long idMateriaRequisito) {
        this.idMateriaRequisito = idMateriaRequisito;
    }

    public void setMateriaDestino(Materia materiaDestino) {
        this.materiaDestino = materiaDestino;
        if (materiaDestino != null) {
            this.idMateriaDestino = materiaDestino.getIdMateria();
        }
    }

    public void setMateriaRequisito(Materia materiaRequisito) {
        this.materiaRequisito = materiaRequisito;
        if (materiaRequisito != null) {
            this.idMateriaRequisito = materiaRequisito.getIdMateria();
        }
    }


    public void setTipoRequisito(TipoRequisito tipoRequisito) {
        this.tipoRequisito = tipoRequisito;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Correlatividad that = (Correlatividad) o;
        return Objects.equals(idMateriaDestino, that.idMateriaDestino) &&
               Objects.equals(idMateriaRequisito, that.idMateriaRequisito);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMateriaDestino, idMateriaRequisito);
    }

}
