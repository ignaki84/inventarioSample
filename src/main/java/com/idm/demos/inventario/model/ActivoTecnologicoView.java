package com.idm.demos.inventario.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Immutable
@Table(name = "v_activos_tecnologicos")
public class ActivoTecnologicoView {
	@Id
    @Column(name = "FC_IDPRODUCTO")
    private String idProducto;

    @Column(name = "FC_FOLIO")
    private String folio;

    @Column(name = "FC_SERIE")
    private String serie;

    @Column(name = "FC_MARCA")
    private String marca;

    @Column(name = "ESTADO")
    private String estado;

    @Column(name = "FN_COSTO")
    private BigDecimal costo;

    @Column(name = "FD_CREACION")
    private LocalDateTime creacion;

    @Column(name = "FC_CODPREF")
    private String codPref;

    @Column(name = "CATEGORIA")
    private String categoria;
}
