package com.zebop.sistemasCorrelativas.models;

import com.zebop.sistemasCorrelativas.util.Constantes;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Year;
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
        if (carrera == null) {
            throw new IllegalArgumentException("El alumno debe pertenecer a una carrera.");
        }
        this.carrera = carrera;
    }


    public void setLegajo(String legajo) {
        if (legajo == null || legajo.isBlank()) {
            throw new IllegalArgumentException("El legajo del alumno es obligatorio.");
        }
        if (legajo.trim().length() > Constantes.LONGITUD_MAXIMA_LEGAJO) {
            throw new IllegalArgumentException("El legajo no puede superar los " + Constantes.LONGITUD_MAXIMA_LEGAJO + " caracteres.");
        }
        this.legajo = legajo.trim();
    }


    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del alumno es obligatorio.");
        }
        if (nombre.trim().length() > Constantes.LONGITUD_MAXIMA_NOMBRE_ALUMNO) {
            throw new IllegalArgumentException("El nombre no puede superar los " + Constantes.LONGITUD_MAXIMA_NOMBRE_ALUMNO + " caracteres.");
        }
        this.nombre = nombre.trim();
    }


    public void setApellido(String apellido) {
        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido del alumno es obligatorio.");
        }
        if (apellido.trim().length() > Constantes.LONGITUD_MAXIMA_APELLIDO_ALUMNO) {
            throw new IllegalArgumentException("El apellido no puede superar los " + Constantes.LONGITUD_MAXIMA_APELLIDO_ALUMNO + " caracteres.");
        }
        this.apellido = apellido.trim();
    }

    public void setAnioIngreso(Integer anioIngreso) {
        if (anioIngreso == null) {
            throw new IllegalArgumentException("El año de ingreso es obligatorio.");
        }
        int anioActual = Year.now().getValue();
        if (anioIngreso < 2000 || anioIngreso > anioActual) {
            throw new IllegalArgumentException("El año de ingreso debe estar entre 2000 y " + anioActual + ".");
        }
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
