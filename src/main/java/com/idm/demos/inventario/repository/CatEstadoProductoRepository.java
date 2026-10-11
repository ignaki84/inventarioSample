package com.idm.demos.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.idm.demos.inventario.model.CatEstadoProducto;

//@Repository
public interface CatEstadoProductoRepository extends JpaRepository<CatEstadoProducto, Integer> {

}
