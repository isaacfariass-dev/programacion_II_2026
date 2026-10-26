package com.zebop.sistemasCorrelativas.models;

import com.zebop.sistemasCorrelativas.models.enums.EstadoMateria;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.*;
/**
 * Entidad que registra el estado académico y calificaciones de un alumno en una materia.
 * Mapea la tabla 'historial_academico' de la base de datos.
 */
@Entity
@Getter
@NoArgsConstructor 
@AllArgsConstructor
@ToString
@Table(name = "historial_academico", uniqueConstraints = {
    @UniqueConstraint(name = "uk_alumno_materia", columnNames = {"id_alumno", "id_materia"})
})
public class HistorialAcademico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Long idHistorial;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_alumno", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoMateria estado;

    @Column(name = "nota_final")
    private Double notaFinal;

    @Column(name = "ultima_modificacion", insertable = false, updatable = false)
    private LocalDateTime ultimaModificacion;

    public void setIdHistorial(Long idHistorial) {
        this.idHistorial = idHistorial;
    }


    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public void setEstado(EstadoMateria estado) {
        this.estado = estado;
    }

    public void setNotaFinal(Double notaFinal) {
        this.notaFinal = notaFinal;
    }

    public void setUltimaModificacion(LocalDateTime ultimaModificacion) {
        this.ultimaModificacion = ultimaModificacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistorialAcademico that = (HistorialAcademico) o;
        return Objects.equals(idHistorial, that.idHistorial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idHistorial);
    }

}
