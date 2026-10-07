package com.zebop.sistemasCorrelativas.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
//implementar la interfaz serializable es un Estándar de los Java Beans 
// de no ponerlo te larga un NotSerializableExeption 
public class HistorialAcademico implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idHistorial;
    private Long idAlumno;
    private Long idMateria;
    private String estado;
    private Double notaFinal;
    private LocalDateTime ultimaModificacion;

    public HistorialAcademico() {
    }

    public HistorialAcademico(Long idHistorial, Long idAlumno, Long idMateria, String estado, Double notaFinal, LocalDateTime ultimaModificacion) {
        this.idHistorial = idHistorial;
        this.idAlumno = idAlumno;
        this.idMateria = idMateria;
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

    public Long getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(Long idAlumno) {
        this.idAlumno = idAlumno;
    }

    public Long getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(Long idMateria) {
        this.idMateria = idMateria;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
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
                ", idAlumno=" + idAlumno +
                ", idMateria=" + idMateria +
                ", estado='" + estado + '\'' +
                ", notaFinal=" + notaFinal +
                ", ultimaModificacion=" + ultimaModificacion +
                '}';
    }
}