package com.idm.demos.inventario.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.idm.demos.inventario.model.HistorialEstado;

//@Repository
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Integer> {
	List<HistorialEstado> findByActivoTecnologico_IdProductoOrderByFechaDesc(String idProducto);
}
