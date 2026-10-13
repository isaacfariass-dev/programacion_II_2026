package com.zebop.sistemasCorrelativas.entity;

import com.zebop.sistemasCorrelativas.entity.enums.Rol;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.*;
/**
 * Entidad que gestiona las credenciales y roles de acceso.
 * Mapea la tabla 'usuario' de la base de datos.
 * Posee una relación 1:1 opcional con Alumno (NULL para administradores).
 */
@Entity
@Getter
@NoArgsConstructor 
@AllArgsConstructor
@ToString
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_alumno", unique = true, nullable = true)
    private Alumno alumno;

    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @Column(name = "clave", nullable = false, length = 255)
    private String clave;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;


    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(idUsuario, usuario.idUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario);
    }
}
