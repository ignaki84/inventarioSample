package com.idm.demos.inventario.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.idm.demos.inventario.controller.request.ActivoTecUpdateRequestDTO;
import com.idm.demos.inventario.controller.request.ActivoTecnologicoRequestDto;
import com.idm.demos.inventario.controller.response.ReporteExportacionDto;
import com.idm.demos.inventario.model.ActivoTecnologicoView;

public interface ActivoTecnologicoService {
	ActivoTecnologicoView registrarActivo(ActivoTecnologicoRequestDto requestDto, String idUsuario);
	ActivoTecnologicoView actualizarActivo(String idProducto, ActivoTecUpdateRequestDTO requestDto, String idUsuario);
    ActivoTecnologicoView cambiarEstado(String idProducto, Integer idNuevoEstado, String idUsuario);
    Page<ActivoTecnologicoView> buscarActivos(String serie, String marca, String estado, String categoria, BigDecimal costoMin, BigDecimal costoMax, Pageable pageable, String idUsuario);
    ReporteExportacionDto generarReporteAuditoria(String serie, String marca, String estado, String categoria, BigDecimal costoMin, BigDecimal costoMax, String idUsuario);
}
