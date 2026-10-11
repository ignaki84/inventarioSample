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
@Table(name = "HISTORIAL_ESTADO")
public class HistorialEstado {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FN_IDHISTORIAL")
    private Integer idHistorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FC_IDPRODUCTO", nullable = false)
    private ActivoTecnologico activoTecnologico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_ESTADO_ANTERIOR")
    private CatEstadoProducto estadoAnterior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FN_ESTADO_NUEVO", nullable = false)
    private CatEstadoProducto estadoNuevo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FC_IDUSUARIO", nullable = false)
    private Usuario usuario;

    @Column(name = "FD_FECHA", insertable = false, updatable = false)
    private LocalDateTime fecha;

    @Column(name = "FC_COMENTARIOS", length = 500)
    private String comentarios;
}
