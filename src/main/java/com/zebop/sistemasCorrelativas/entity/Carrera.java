package com.zebop.sistemasCorrelativas.entity;

import java.io.Serializable;
import java.util.Objects;

public class Carrera implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idCarrera;
    private String codigo;
    private String nombre;
    private String institucion;
    private Integer duracionAnios;

    // Constructor sin argumentos
    public Carrera() {
    }

    // Constructor con argumentos
    public Carrera(Long idCarrera, String codigo, String nombre, String institucion, Integer duracionAnios) {
        this.idCarrera = idCarrera;
        this.codigo = codigo;
        this.nombre = nombre;
        this.institucion = institucion;
        this.duracionAnios = duracionAnios;
    }

    // Getters y Setters
    public Long getIdCarrera() {
        return idCarrera;
    }

    public void setIdCarrera(Long idCarrera) {
        this.idCarrera = idCarrera;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public Integer getDuracionAnios() {
        return duracionAnios;
    }

    public void setDuracionAnios(Integer duracionAnios) {
        this.duracionAnios = duracionAnios;
    }

    // equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Carrera carrera = (Carrera) o;
        return Objects.equals(idCarrera, carrera.idCarrera);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCarrera);
    }

    @Override
    public String toString() {
        return "Carrera{" +
                "idCarrera=" + idCarrera +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", institucion='" + institucion + '\'' +
                ", duracionAnios=" + duracionAnios +
                '}';
    }
}