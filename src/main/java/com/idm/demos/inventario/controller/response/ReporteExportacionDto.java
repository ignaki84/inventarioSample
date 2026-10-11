package com.idm.demos.inventario.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReporteExportacionDto {
	private String fileName;
    private String fileBase64;
}
