package com.idm.demos.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.idm.demos.inventario.model.ActivoTecnologico;

public interface ActivoTecnologicoRepository extends JpaRepository<ActivoTecnologico, String> {
	boolean existsBySerie(String serie);
	    
    @Query(value="SELECT NEXTVAL(seq_product_id)", nativeQuery = true)
    Long obtenerSiguienteValorSecuencia();
}
