package com.idm.demos.inventario.controller;

import java.math.BigDecimal;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.idm.demos.inventario.controller.request.ActivoTecUpdateRequestDTO;
import com.idm.demos.inventario.controller.request.ActivoTecnologicoRequestDto;
import com.idm.demos.inventario.controller.request.CambioEstadoRequestDTO;
import com.idm.demos.inventario.controller.response.ApiResponse;
import com.idm.demos.inventario.controller.response.ReporteExportacionDto;
import com.idm.demos.inventario.model.ActivoTecnologicoView;
import com.idm.demos.inventario.service.ActivoTecnologicoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/activos")
@RequiredArgsConstructor
public class ActivoTecnologicoController {
	
	private final ActivoTecnologicoService activoService;

	
	/**
     * Método auxiliar para extraer el ID del usuario.
     * Cuando el filtro JWT esté listo, inyectará los datos del token en el SecurityContext.
     */
	private String obtenerIdUsuarioDelToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // El filtro asignará el ID de usuario como "Name" (Principal)
        }
        // Valor temporal por defecto mientras no tengamos la capa de seguridad activa para poder probar en Postman
        return "USR-TEST"; 
    }
	
	@PostMapping
    public ResponseEntity<ApiResponse<ActivoTecnologicoView>> registrarActivo(
    		@RequestBody ActivoTecnologicoRequestDto requestDto) {
        String idUsuario = obtenerIdUsuarioDelToken();
        ActivoTecnologicoView guardado = activoService.registrarActivo(requestDto, idUsuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Activo registrado con éxito", guardado));
    }
	
	@PutMapping("/{idProducto}")
    public ResponseEntity<ApiResponse<ActivoTecnologicoView>> actualizarActivo(
            @PathVariable String idProducto, 
            @RequestBody ActivoTecUpdateRequestDTO requestDto) {
        String idUsuario = obtenerIdUsuarioDelToken();
        ActivoTecnologicoView actualizado = activoService.actualizarActivo(idProducto, requestDto, idUsuario);
        return ResponseEntity.ok(ApiResponse.success("Activo actualizado con éxito", actualizado));
    }
	
	@PatchMapping("/{idProducto}/estado")
    public ResponseEntity<ApiResponse<Void>> cambiarEstado(
            @PathVariable String idProducto, 
            @RequestBody CambioEstadoRequestDTO request) {
        String idUsuario = obtenerIdUsuarioDelToken();
        activoService.cambiarEstado(idProducto, request.getIdNuevoEstado(), idUsuario); 
        return ResponseEntity.ok(ApiResponse.success("Estado modificado con éxito", null));
    }
	
	@GetMapping
    public ResponseEntity<ApiResponse<Page<ActivoTecnologicoView>>> buscarActivos(
            @RequestParam(required = false) String serie,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) BigDecimal costoMin,
            @RequestParam(required = false) BigDecimal costoMax,
            @ParameterObject @PageableDefault(size = 10, sort = "creacion", direction = Sort.Direction.DESC) Pageable pageable) {
        String idUsuario = obtenerIdUsuarioDelToken();
        Page<ActivoTecnologicoView> resultados = activoService.buscarActivos(
                serie, marca, estado, categoria, costoMin, costoMax, pageable, idUsuario);
        
        return ResponseEntity.ok(ApiResponse.success("Búsqueda exitosa", resultados));
    }
	
	@GetMapping("/auditoria")
    public ResponseEntity<ApiResponse<ReporteExportacionDto>> generarAuditoria(
            @RequestParam(required = false) String serie,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) BigDecimal costoMin,
            @RequestParam(required = false) BigDecimal costoMax) {
        String idUsuario = obtenerIdUsuarioDelToken();
        ReporteExportacionDto reporte = activoService.generarReporteAuditoria(
                serie, marca, estado, categoria, costoMin, costoMax, idUsuario);
        
        return ResponseEntity.ok(ApiResponse.success("Reporte generado correctamente", reporte));
    }
}
