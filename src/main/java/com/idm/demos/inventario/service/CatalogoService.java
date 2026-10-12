package com.idm.demos.inventario.service;

import java.util.List;

import com.idm.demos.inventario.model.CatAccion;
import com.idm.demos.inventario.model.CatCategoria;
import com.idm.demos.inventario.model.CatEstadoProducto;
import com.idm.demos.inventario.model.CatRol;

public interface CatalogoService {
	List<CatEstadoProducto> obtenerEstados();
	List<CatCategoria> obetenerCategorias();
	List<CatRol> obtenerRoles();
	List<CatAccion> obtenerAcciones();
	
	void limpiarCacheCatalogos();

}
