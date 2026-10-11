package com.idm.demos.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.idm.demos.inventario.model.ActivoTecnologicoView;

public interface ActivoTecnologicoViewRepository extends 
	JpaRepository<ActivoTecnologicoView, String>,
	JpaSpecificationExecutor<ActivoTecnologicoView>
{
//	List<ActivoTecnologicoView> findByEstado(String estado);
//  List<ActivoTecnologicoView> findByCategoria(String categoria);
    
//    Número de serie.
//    Marca o modelo.
//    Rango de costo de adquisición.
}
