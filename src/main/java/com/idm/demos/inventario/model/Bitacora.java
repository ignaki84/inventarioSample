package com.idm.demos.inventario.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "BITACORA")
public class Bitacora {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FN_IDBITACORA")
    private Integer idBitacora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FC_IDUSUARIO", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_IDACCION", nullable = false)
    private CatAccion accion;

    @Column(name = "FD_FECHA", insertable = false, updatable = false)
    private LocalDateTime fecha;

    @Column(name = "FC_DESCRIPCION", length = 500)
    private String descripcion;
}
