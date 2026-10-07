package com.zebop.sistemasCorrelativas.entity;

import java.io.Serializable;
import java.util.Objects;

public class Correlatividad implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idMateriaDestino;
    private Long idMateriaRequisito;
    private String tipoRequisito;

    public Correlatividad() {
    }

    public Correlatividad(Long idMateriaDestino, Long idMateriaRequisito, String tipoRequisito) {
        this.idMateriaDestino = idMateriaDestino;
        this.idMateriaRequisito = idMateriaRequisito;
        this.tipoRequisito = tipoRequisito;
    }

    public Long getIdMateriaDestino() {
        return idMateriaDestino;
    }

    public void setIdMateriaDestino(Long idMateriaDestino) {
        this.idMateriaDestino = idMateriaDestino;
    }

    public Long getIdMateriaRequisito() {
        return idMateriaRequisito;
    }

    public void setIdMateriaRequisito(Long idMateriaRequisito) {
        this.idMateriaRequisito = idMateriaRequisito;
    }

    public String getTipoRequisito() {
        return tipoRequisito;
    }

    public void setTipoRequisito(String tipoRequisito) {
        this.tipoRequisito = tipoRequisito;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Correlatividad correlatividad = (Correlatividad) o;
        return Objects.equals(idMateriaDestino, correlatividad.idMateriaDestino) &&
               Objects.equals(idMateriaRequisito, correlatividad.idMateriaRequisito);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMateriaDestino, idMateriaRequisito);
    }

    @Override
    public String toString() {
        return "Correlatividad{" +
                "idMateriaDestino=" + idMateriaDestino +
                ", idMateriaRequisito=" + idMateriaRequisito +
                ", tipoRequisito='" + tipoRequisito + '\'' +
                '}';
    }
}