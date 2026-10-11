package com.idm.demos.inventario.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UsuarioService {
	public UserDetails loadUserByUsername(String idUsuario) throws UsernameNotFoundException;
	public UserDetails cargarUsuarioPorId(String idUsuario);
}
