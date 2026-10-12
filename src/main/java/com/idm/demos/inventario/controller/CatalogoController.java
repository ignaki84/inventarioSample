package com.idm.demos.inventario.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.idm.demos.inventario.controller.response.ApiResponse;
import com.idm.demos.inventario.model.CatCategoria;
import com.idm.demos.inventario.model.CatEstadoProducto;
import com.idm.demos.inventario.service.CatalogoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/apiV1/catalogos")
@RequiredArgsConstructor
public class CatalogoController {
	private final CatalogoService catalogoService;
	
	@GetMapping("/categorias")
	public ResponseEntity<ApiResponse<List<CatCategoria>>>obtenerCategorias(){
		List<CatCategoria> categorias = catalogoService.obetenerCategorias();
        return ResponseEntity.ok(ApiResponse.success("Categorías obtenidas con éxito", categorias));
	}
	
	@GetMapping("/estados")
    public ResponseEntity<ApiResponse<List<CatEstadoProducto>>> obtenerEstados() {
        List<CatEstadoProducto> estados = catalogoService.obtenerEstados();
        return ResponseEntity.ok(ApiResponse.success("Estados obtenidos con éxito", estados));
    }

}
