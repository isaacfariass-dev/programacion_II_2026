package com.zebop.sistemasCorrelativas.models;

import com.zebop.sistemasCorrelativas.models.enums.Rol;
import com.zebop.sistemasCorrelativas.util.Constantes;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.*;

/**
 * Entidad que representa las credenciales de acceso de un Alumno al sistema.
 * Mapea la tabla 'usuario', en relación 1:1 con 'alumno' (cada alumno tiene a lo sumo un usuario).
 * No se usa todavía para autenticar: queda preparada para una futura implementación de login.
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
    @JoinColumn(name = "id_alumno", nullable = false, unique = true)
    private Alumno alumno;

    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @Column(name = "clave", nullable = false, length = 255)
    private String clave;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public Usuario(Alumno alumno, String nombreUsuario, String clave, Rol rol) {
        setAlumno(alumno);
        setNombreUsuario(nombreUsuario);
        setClave(clave);
        setRol(rol);
        this.fechaCreacion = LocalDateTime.now();
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setAlumno(Alumno alumno) {
        if (alumno == null || alumno.getIdAlumno() == null) {
            throw new IllegalArgumentException("El usuario debe estar asociado a un alumno.");
        }
        this.alumno = alumno;
    }

    public void setNombreUsuario(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (nombreUsuario.trim().length() > Constantes.LONGITUD_MAXIMA_NOMBRE_USUARIO) {
            throw new IllegalArgumentException("El nombre de usuario no puede superar los "
                    + Constantes.LONGITUD_MAXIMA_NOMBRE_USUARIO + " caracteres.");
        }
        this.nombreUsuario = nombreUsuario.trim();
    }

    public void setClave(String clave) {
        if (clave == null || clave.isBlank()) {
            throw new IllegalArgumentException("La clave del usuario es obligatoria.");
        }
        if (clave.length() > Constantes.LONGITUD_MAXIMA_CLAVE) {
            throw new IllegalArgumentException("La clave no puede superar los "
                    + Constantes.LONGITUD_MAXIMA_CLAVE + " caracteres.");
        }
        this.clave = clave;
    }

    public void setRol(Rol rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol del usuario es obligatorio.");
        }
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
