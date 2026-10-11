package com.idm.demos.inventario.service.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idm.demos.inventario.model.CatCategoria;
import com.idm.demos.inventario.model.CatEstadoProducto;
import com.idm.demos.inventario.repository.CatCategoriaRepository;
import com.idm.demos.inventario.repository.CatEstadoProductoRepository;
import com.idm.demos.inventario.service.CatalogoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CatalogoServiceImpl implements CatalogoService {
	//TODO:otros catalogos cacheables???
	private final CatEstadoProductoRepository edoRepo;
	private final CatCategoriaRepository catRepo;

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

	@CacheEvict(value= {"estadosCache","categoriasCache"}, allEntries = true)
	public void limpiarCacheCatalogos() {	

	}

}
