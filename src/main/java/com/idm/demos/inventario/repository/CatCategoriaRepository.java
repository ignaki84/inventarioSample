package com.idm.demos.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.idm.demos.inventario.model.CatCategoria;

//@Repository
public interface CatCategoriaRepository extends JpaRepository<CatCategoria, Integer> {
	//TODO: revisar si es necesario
	boolean existsByCodPref(String codPref);
}
