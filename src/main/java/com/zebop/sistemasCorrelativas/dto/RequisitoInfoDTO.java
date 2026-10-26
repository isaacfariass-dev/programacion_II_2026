package com.zebop.sistemasCorrelativas.dto;

import com.zebop.sistemasCorrelativas.models.enums.TipoRequisito;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa el estado de cumplimiento de una correlatividad previa para una materia.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequisitoInfoDTO {
    private Long idMateria;
    private String codigoMateria;
    private String nombreMateria;
    private TipoRequisito tipoRequisito;
    private boolean cumplido;
    private String estadoActualTexto;
}
