package com.idm.demos.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.idm.demos.inventario.model.ActivoTecnologico;

public interface ActivoTecnologicoRepository extends JpaRepository<ActivoTecnologico, String> {
	boolean existsBySerie(String serie);
	
	// TODO:mejorar esto Consulta para obtener el folio máximo de una categoría en un año específico
    @Query("SELECT MAX(a.folio) FROM ActivoTecnologico a WHERE a.categoria.idCategoria = :idCategoria AND a.folio LIKE CONCAT(:prefijo, :anio, '%')")
    String findMaxFolioByCategoriaAndAnio(@Param("idCategoria") Integer idCategoria, @Param("prefijo") String prefijo, @Param("anio") String anio);
}
