package com.zebop.sistemasCorrelativas.entity;

import com.zebop.sistemasCorrelativas.entity.enums.Cuatrimestre;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entidad que representa una Asignatura/Materia perteneciente a un plan de carrera.
 * Mapea la tabla 'materia' de la base de datos.
 */
@Entity
@Table(name = "materia")
public class Materia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_materia")
    private Long idMateria;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_carrera", nullable = false)
    private Carrera carrera;

    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "anio_plan", nullable = false)
    private Integer anioPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "cuatrimestre", nullable = false)
    private Cuatrimestre cuatrimestre;

    @Column(name = "carga_horaria", nullable = false)
    private Integer cargaHoraria;

    public Materia() {
    }

    public Materia(Long idMateria, Carrera carrera, String codigo, String nombre, Integer anioPlan, Cuatrimestre cuatrimestre, Integer cargaHoraria) {
        this.idMateria = idMateria;
        this.carrera = carrera;
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

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
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

    public Cuatrimestre getCuatrimestre() {
        return cuatrimestre;
    }

    public void setCuatrimestre(Cuatrimestre cuatrimestre) {
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
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", anioPlan=" + anioPlan +
                ", cuatrimestre=" + cuatrimestre +
                ", cargaHoraria=" + cargaHoraria +
                '}';
    }
}
