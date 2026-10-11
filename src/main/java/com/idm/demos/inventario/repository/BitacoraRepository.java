package com.idm.demos.inventario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.idm.demos.inventario.model.Bitacora;

//@Repository
public interface BitacoraRepository extends JpaRepository<Bitacora, Integer>{
	List<Bitacora> findByUsuario_IdUsuarioOrderByFechaDesc(String idUsuario);

}
