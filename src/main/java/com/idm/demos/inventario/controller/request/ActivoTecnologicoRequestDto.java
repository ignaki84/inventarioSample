package com.idm.demos.inventario.controller.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ActivoTecnologicoRequestDto {
	private String serie;
    private String marca;
    private Integer idCategoria;
    //private Integer idEstatus;
    private BigDecimal costo;
}
