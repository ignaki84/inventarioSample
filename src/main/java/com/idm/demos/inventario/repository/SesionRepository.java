package com.idm.demos.inventario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.idm.demos.inventario.model.Sesion;

//@Repository
public interface SesionRepository extends JpaRepository<Sesion, String> {
	Optional<Sesion> findByTokenAndEstado(String token, Integer estado);
	Optional<Sesion> findByRefreshTokenAndEstado(String refreshToken, Integer estado);
}
