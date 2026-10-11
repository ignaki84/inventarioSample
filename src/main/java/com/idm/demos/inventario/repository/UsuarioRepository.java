package com.idm.demos.inventario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.idm.demos.inventario.model.Usuario;

//@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
	Optional<Usuario> findByEmail(String email);
}
