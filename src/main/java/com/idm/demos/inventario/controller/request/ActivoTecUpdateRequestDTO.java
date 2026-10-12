package com.idm.demos.inventario.controller.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ActivoTecUpdateRequestDTO {
	private String serie;
    private String marca;
    private BigDecimal costo;

}
