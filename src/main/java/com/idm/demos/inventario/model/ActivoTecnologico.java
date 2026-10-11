package com.idm.demos.inventario.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ACTIVO_TECNOLOGICO")
public class ActivoTecnologico {
	@Id
    @Column(name = "FC_IDPRODUCTO", length = 40, updatable = false)
    private String idProducto;

    @Column(name = "FC_FOLIO", nullable = false, length = 16, updatable = false)
    private String folio;

    @Column(name = "FC_SERIE", nullable = false, length = 20)
    private String serie;

    @Column(name = "FC_MARCA", nullable = false)
    private String marca;

    @Column(name = "FN_COSTO", nullable = false)
    private BigDecimal costo;

    @Column(name = "FD_CREACION", insertable = false, updatable = false)
    private LocalDateTime creacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_IDCATEGORIA", updatable = false)
    private CatCategoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_ESTATUS")
    private CatEstadoProducto estadoProducto;
}
