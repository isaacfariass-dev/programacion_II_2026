package com.zebop.sistemasCorrelativas.models;

import com.zebop.sistemasCorrelativas.models.enums.Cuatrimestre;
import com.zebop.sistemasCorrelativas.util.Constantes;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import lombok.*;


/**
 * Entidad que representa una Asignatura/Materia perteneciente a un plan de carrera.
 * Mapea la tabla 'materia' de la base de datos.
 */
@Entity
@Getter
@NoArgsConstructor 
@AllArgsConstructor
@ToString
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



    public void setIdMateria(Long idMateria) {
        this.idMateria = idMateria;
    }
    public void setCarrera(Carrera carrera) {
        if (carrera == null) {
            throw new IllegalArgumentException("La materia debe pertenecer a una carrera.");
        }
        this.carrera = carrera;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de la materia es obligatorio.");
        }
        if (codigo.trim().length() > Constantes.LONGITUD_MAXIMA_CODIGO_MATERIA) {
            throw new IllegalArgumentException("El código de la materia no puede superar los " + Constantes.LONGITUD_MAXIMA_CODIGO_MATERIA + " caracteres.");
        }
        this.codigo = codigo.trim();
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la materia es obligatorio.");
        }
        if (nombre.trim().length() > Constantes.LONGITUD_MAXIMA_NOMBRE_MATERIA) {
            throw new IllegalArgumentException("El nombre de la materia no puede superar los " + Constantes.LONGITUD_MAXIMA_NOMBRE_MATERIA + " caracteres.");
        }
        this.nombre = nombre.trim();
    }

    public void setAnioPlan(Integer anioPlan) {
        if (anioPlan == null) {
            throw new IllegalArgumentException("El año del plan es obligatorio.");
        }
        if (anioPlan < Constantes.ANIO_PLAN_MINIMO || anioPlan > Constantes.ANIO_PLAN_MAXIMO) {
            throw new IllegalArgumentException("El año del plan debe estar entre " + Constantes.ANIO_PLAN_MINIMO
                    + " y " + Constantes.ANIO_PLAN_MAXIMO + ".");
        }
        this.anioPlan = anioPlan;
    }


    public void setCuatrimestre(Cuatrimestre cuatrimestre) {
        if (cuatrimestre == null) {
            throw new IllegalArgumentException("El cuatrimestre es obligatorio.");
        }
        this.cuatrimestre = cuatrimestre;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        if (cargaHoraria == null) {
            throw new IllegalArgumentException("La carga horaria es obligatoria.");
        }
        if (cargaHoraria < Constantes.CARGA_HORARIA_MINIMA) {
            throw new IllegalArgumentException("La carga horaria debe ser mayor o igual a " + Constantes.CARGA_HORARIA_MINIMA + ".");
        }
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

}
