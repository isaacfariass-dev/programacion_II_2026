package com.zebop.sistemasCorrelativas.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entidad que representa a un Alumno o Estudiante.
 * Mapea la tabla 'alumno' de la base de datos.
 */
@Entity
@Table(name = "alumno")
public class Alumno implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alumno")
    private Long idAlumno;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_carrera", nullable = false)
    private Carrera carrera;

    @Column(name = "legajo", nullable = false, unique = true, length = 20)
    private String legajo;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 50)
    private String apellido;

    @Column(name = "anio_ingreso", nullable = false)
    private Integer anioIngreso;

    public Alumno() {
    }

    public Alumno(Long idAlumno, Carrera carrera, String legajo, String nombre, String apellido, Integer anioIngreso) {
        this.idAlumno = idAlumno;
        this.carrera = carrera;
        this.legajo = legajo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.anioIngreso = anioIngreso;
    }

    public Long getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(Long idAlumno) {
        this.idAlumno = idAlumno;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Integer getAnioIngreso() {
        return anioIngreso;
    }

    public void setAnioIngreso(Integer anioIngreso) {
        this.anioIngreso = anioIngreso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alumno alumno = (Alumno) o;
        return Objects.equals(idAlumno, alumno.idAlumno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAlumno);
    }

    @Override
    public String toString() {
        return "Alumno{" +
                "idAlumno=" + idAlumno +
                ", legajo='" + legajo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", anioIngreso=" + anioIngreso +
                '}';
    }
}
