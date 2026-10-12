package com.idm.demos.inventario.service.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idm.demos.inventario.model.CatAccion;
import com.idm.demos.inventario.model.CatCategoria;
import com.idm.demos.inventario.model.CatEstadoProducto;
import com.idm.demos.inventario.model.CatRol;
import com.idm.demos.inventario.repository.CatCategoriaRepository;
import com.idm.demos.inventario.repository.CatEstadoProductoRepository;
import com.idm.demos.inventario.repository.CatRolRepository;
import com.idm.demos.inventario.repository.CatAccionRepository;
import com.idm.demos.inventario.service.CatalogoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CatalogoServiceImpl implements CatalogoService {
	//TODO:otros catalogos cacheables???
	private final CatEstadoProductoRepository edoRepo;
	private final CatCategoriaRepository catRepo;
	private final CatRolRepository rolRepo;
	private final CatAccionRepository accionRepo;

	@Cacheable("estadosCache")
	@Transactional(readOnly = true)
	public List<CatEstadoProducto> obtenerEstados() {
		return edoRepo.findAll();
	}

	@Cacheable("categoriasCache")
	@Transactional(readOnly = true)
	public List<CatCategoria> obetenerCategorias() {
		return catRepo.findAll();
	}
	
	@Cacheable("rolesCache")
	@Transactional(readOnly = true)
	public List<CatRol> obtenerRoles(){
		return rolRepo.findAll();
	}
	
	@Cacheable("accionesCache")
	@Transactional(readOnly = true)
	public List<CatAccion> obtenerAcciones(){
		return accionRepo.findAll();
	}

	@CacheEvict(
		value= {"estadosCache","categoriasCache","rolesCache","accionesCache"},
		allEntries = true)
	public void limpiarCacheCatalogos() {	

	}

}
