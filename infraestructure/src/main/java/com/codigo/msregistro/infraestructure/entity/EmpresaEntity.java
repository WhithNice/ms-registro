package com.codigo.msregistro.infraestructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "empresa")
@Getter
@Setter
public class EmpresaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    private Long idEmpresa;
    @Column(name = "num_docu")
    private String numDocu;
    @Column(name = "razon_social")
    private String razonSocial;
    @Column(name = "nom_comercial")
    private String nomComercial;
    @Column(name = "estado")
    private Integer estado;
    @Column(name = "usua_crea")
    private String usuaCrea;
    @Column(name = "date_create")
    private Timestamp dateCreate;
    @Column(name = "usua_modif")
    private String usuaModif;
    @Column(name = "date_modif")
    private Timestamp dateModif;
    @Column(name = "usua_delet")
    private String usuaDelet;
    @Column(name = "date_delet")
    private Timestamp dateDelet;
    @ManyToOne(optional = false)
    @JoinColumn(name = "tipo_documento_id", nullable = false)
    private TipoDocumentoEntity tipoDocumento;
}