package com.zebop.sistemasCorrelativas.entity;

import com.zebop.sistemasCorrelativas.entity.enums.EstadoMateria;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que registra el estado académico y calificaciones de un alumno en una materia.
 * Mapea la tabla 'historial_academico' de la base de datos.
 */
@Entity
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

    public HistorialAcademico() {
    }

    public HistorialAcademico(Long idHistorial, Alumno alumno, Materia materia, EstadoMateria estado, Double notaFinal, LocalDateTime ultimaModificacion) {
        this.idHistorial = idHistorial;
        this.alumno = alumno;
        this.materia = materia;
        this.estado = estado;
        this.notaFinal = notaFinal;
        this.ultimaModificacion = ultimaModificacion;
    }

    public Long getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(Long idHistorial) {
        this.idHistorial = idHistorial;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public EstadoMateria getEstado() {
        return estado;
    }

    public void setEstado(EstadoMateria estado) {
        this.estado = estado;
    }

    public Double getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(Double notaFinal) {
        this.notaFinal = notaFinal;
    }

    public LocalDateTime getUltimaModificacion() {
        return ultimaModificacion;
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

    @Override
    public String toString() {
        return "HistorialAcademico{" +
                "idHistorial=" + idHistorial +
                ", estado=" + estado +
                ", notaFinal=" + notaFinal +
                ", ultimaModificacion=" + ultimaModificacion +
                '}';
    }
}
