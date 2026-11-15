package com.zebop.sistemasCorrelativas.models;

import com.zebop.sistemasCorrelativas.models.enums.TipoRequisito;
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
        setMateriaDestino(materiaDestino);
        setMateriaRequisito(materiaRequisito);
        setTipoRequisito(tipoRequisito);
    }

    public void setIdMateriaDestino(Long idMateriaDestino) {
        if (idMateriaDestino == null) {
            throw new IllegalArgumentException("Debe indicarse la materia destino de la correlatividad.");
        }
        this.idMateriaDestino = idMateriaDestino;
    }
    public void setIdMateriaRequisito(Long idMateriaRequisito) {
        if (idMateriaRequisito == null) {
            throw new IllegalArgumentException("Debe indicarse la materia requisito de la correlatividad.");
        }
        this.idMateriaRequisito = idMateriaRequisito;
    }

    public void setMateriaDestino(Materia materiaDestino) {
        if (materiaDestino == null || materiaDestino.getIdMateria() == null) {
            throw new IllegalArgumentException("Debe indicarse la materia destino de la correlatividad.");
        }
        if (materiaRequisito != null && Objects.equals(materiaDestino.getIdMateria(), materiaRequisito.getIdMateria())) {
            throw new IllegalArgumentException("Una materia no puede ser correlativa de sí misma.");
        }
        this.materiaDestino = materiaDestino;
        this.idMateriaDestino = materiaDestino.getIdMateria();
    }

    public void setMateriaRequisito(Materia materiaRequisito) {
        if (materiaRequisito == null || materiaRequisito.getIdMateria() == null) {
            throw new IllegalArgumentException("Debe indicarse la materia requisito de la correlatividad.");
        }
        if (materiaDestino != null && Objects.equals(materiaRequisito.getIdMateria(), materiaDestino.getIdMateria())) {
            throw new IllegalArgumentException("Una materia no puede ser correlativa de sí misma.");
        }
        this.materiaRequisito = materiaRequisito;
        this.idMateriaRequisito = materiaRequisito.getIdMateria();
    }


    public void setTipoRequisito(TipoRequisito tipoRequisito) {
        if (tipoRequisito == null) {
            throw new IllegalArgumentException("Debe indicarse el tipo de requisito exigido (REGULAR o APROBADA).");
        }
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
