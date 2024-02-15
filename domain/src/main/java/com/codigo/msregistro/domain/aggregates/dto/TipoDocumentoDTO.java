package com.codigo.msregistro.domain.aggregates.dto;

import java.sql.Timestamp;

public class TipoDocumentoDTO {
    private String codTipo;
    private String descTipo;
    private Integer estado;
    private String usuaCrea;
    private Timestamp dateCreate;
    private String usuaModif;
    private Timestamp dateModif;
    private String usuaDelet;
    private Timestamp dateDelet;
}
