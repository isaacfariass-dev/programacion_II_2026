package com.zebop.sistemasCorrelativas.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import lombok.*;
/**
 * Entidad que representa a un Alumno o Estudiante.
 * Mapea la tabla 'alumno' de la base de datos.
 */
@Entity
@Getter
@NoArgsConstructor 
@AllArgsConstructor
@ToString
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

 

    public void setIdAlumno(Long idAlumno) {
        this.idAlumno = idAlumno;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }


    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public void setApellido(String apellido) {
        this.apellido = apellido;
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

}
