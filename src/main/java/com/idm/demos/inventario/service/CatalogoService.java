package com.idm.demos.inventario.service;

import java.util.List;

import com.idm.demos.inventario.model.CatCategoria;
import com.idm.demos.inventario.model.CatEstadoProducto;

public interface CatalogoService {
	List<CatEstadoProducto> obtenerEstados();
	List<CatCategoria> obetenerCategorias();
	
	void limpiarCacheCatalogos();

}
