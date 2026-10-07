package com.zebop.sistemasCorrelativas.entity;

import java.io.Serializable;
import java.util.Objects;

public class Materia implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idMateria;
    private Long idCarrera;
    private String codigo;
    private String nombre;
    private Integer anioPlan;
    private Integer cuatrimestre;
    private Integer cargaHoraria;

    public Materia() {
    }

    public Materia(Long idMateria, Long idCarrera, String codigo, String nombre, Integer anioPlan, Integer cuatrimestre, Integer cargaHoraria) {
        this.idMateria = idMateria;
        this.idCarrera = idCarrera;
        this.codigo = codigo;
        this.nombre = nombre;
        this.anioPlan = anioPlan;
        this.cuatrimestre = cuatrimestre;
        this.cargaHoraria = cargaHoraria;
    }

    public Long getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(Long idMateria) {
        this.idMateria = idMateria;
    }

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

    public Integer getAnioPlan() {
        return anioPlan;
    }

    public void setAnioPlan(Integer anioPlan) {
        this.anioPlan = anioPlan;
    }

    public Integer getCuatrimestre() {
        return cuatrimestre;
    }

    public void setCuatrimestre(Integer cuatrimestre) {
        this.cuatrimestre = cuatrimestre;
    }

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Materia materia = (Materia) o;
        return Objects.equals(idMateria, materia.idMateria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMateria);
    }

    @Override
    public String toString() {
        return "Materia{" +
                "idMateria=" + idMateria +
                ", idCarrera=" + idCarrera +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", anioPlan=" + anioPlan +
                ", cuatrimestre=" + cuatrimestre +
                ", cargaHoraria=" + cargaHoraria +
                '}';
    }
}