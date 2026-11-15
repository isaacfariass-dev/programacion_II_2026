package com.zebop.sistemasCorrelativas.models;

import com.zebop.sistemasCorrelativas.util.Constantes;
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
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de la carrera es obligatorio.");
        }
        if (codigo.trim().length() > Constantes.LONGITUD_MAXIMA_CODIGO_CARRERA) {
            throw new IllegalArgumentException("El código de la carrera no puede superar los " + Constantes.LONGITUD_MAXIMA_CODIGO_CARRERA + " caracteres.");
        }
        this.codigo = codigo.trim();
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la carrera es obligatorio.");
        }
        if (nombre.trim().length() > Constantes.LONGITUD_MAXIMA_NOMBRE_CARRERA) {
            throw new IllegalArgumentException("El nombre de la carrera no puede superar los " + Constantes.LONGITUD_MAXIMA_NOMBRE_CARRERA + " caracteres.");
        }
        this.nombre = nombre.trim();
    }

    public void setInstitucion(String institucion) {
        if (institucion == null || institucion.isBlank()) {
            throw new IllegalArgumentException("La institución de la carrera es obligatoria.");
        }
        if (institucion.trim().length() > Constantes.LONGITUD_MAXIMA_INSTITUCION) {
            throw new IllegalArgumentException("La institución no puede superar los " + Constantes.LONGITUD_MAXIMA_INSTITUCION + " caracteres.");
        }
        this.institucion = institucion.trim();
    }

    public void setDuracionAnios(Integer duracionAnios) {
        if (duracionAnios == null) {
            throw new IllegalArgumentException("La duración de la carrera es obligatoria.");
        }
        if (duracionAnios < Constantes.DURACION_CARRERA_MINIMA_ANIOS || duracionAnios > Constantes.DURACION_CARRERA_MAXIMA_ANIOS) {
            throw new IllegalArgumentException("La duración de la carrera debe estar entre " + Constantes.DURACION_CARRERA_MINIMA_ANIOS
                    + " y " + Constantes.DURACION_CARRERA_MAXIMA_ANIOS + " años.");
        }
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
