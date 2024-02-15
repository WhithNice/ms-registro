package com.codigo.msregistro.domain.aggregates.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)  // si es que hay algun dato que esta nulo al momento de llamarlo (como no se mostrara los datos de auditoria porque esos atrobutos son nuestros, propios), no se mostrara cuando llamemos el endpoint.
public class PersonaDTO {

    private String numDocu;

    private String nombres;

    private String apePat;

    private String apeMat;

    private Integer estado;

    private String usuaCrea;

    private Timestamp dateCreate;

    private String usuaModif;

    private Timestamp dateModif;

    private String usuaDelet;

    private Timestamp dateDelet;
}
