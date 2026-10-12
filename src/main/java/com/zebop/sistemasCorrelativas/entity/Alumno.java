package com.zebop.sistemasCorrelativas.entity;

import java.io.Serializable;
import java.util.Objects;

public class Alumno implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idAlumno;
    private Long idCarrera;
    private String legajo;
    private String nombre;
    private String apellido;
    private Integer anioIngreso;

    public Alumno() {
    }

    public Alumno(Long idAlumno, Long idCarrera, String legajo, String nombre, String apellido, Integer anioIngreso) {
        this.idAlumno = idAlumno;
        this.idCarrera = idCarrera;
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

    public Long getIdCarrera() {
        return idCarrera;
    }

    public void setIdCarrera(Long idCarrera) {
        this.idCarrera = idCarrera;
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
                ", idCarrera=" + idCarrera +
                ", legajo='" + legajo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", anioIngreso=" + anioIngreso +
                '}';
    }
}