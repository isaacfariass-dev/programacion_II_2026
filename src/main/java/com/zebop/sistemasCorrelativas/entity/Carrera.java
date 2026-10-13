package com.zebop.sistemasCorrelativas.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import lombok.*;
/**
 * Entidad que representa una Carrera o Plan de Estudios.
 * Mapea la tabla 'carrera' de la base de datos.
 */
@Entity
@Getter
@NoArgsConstructor 
@AllArgsConstructor
@ToString
@Table(name = "carrera")
public class Carrera implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrera")
    private Long idCarrera;

    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "institucion", nullable = false, length = 100)
    private String institucion;

    @Column(name = "duracion_anios", nullable = false)
    private Integer duracionAnios;


    public void setIdCarrera(Long idCarrera) {
        this.idCarrera = idCarrera;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public void setDuracionAnios(Integer duracionAnios) {
        this.duracionAnios = duracionAnios;
    }

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

}
