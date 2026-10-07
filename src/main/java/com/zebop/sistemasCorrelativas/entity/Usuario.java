package com.zebop.sistemasCorrelativas.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUsuario;
    private Long idAlumno;
    private String nombreUsuario;
    private String clave;
    private Rol rol;
    private LocalDateTime fechaCreacion;

    public Usuario() {
    }

    public Usuario(Long idUsuario, Long idAlumno, String nombreUsuario, String clave, Rol rol, LocalDateTime fechaCreacion) {
        this.idUsuario = idUsuario;
        this.idAlumno = idAlumno;
        this.nombreUsuario = nombreUsuario;
        this.clave = clave;
        this.rol = rol;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(Long idAlumno) {
        this.idAlumno = idAlumno;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
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

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", idAlumno=" + idAlumno +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", clave='" + clave + '\'' +
                ", rol=" + rol +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}