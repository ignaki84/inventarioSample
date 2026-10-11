package com.idm.demos.inventario.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idm.demos.inventario.model.Usuario;
import com.idm.demos.inventario.repository.UsuarioRepository;
import com.idm.demos.inventario.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {
	private final UsuarioRepository usuarioRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String idUsuario) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findById(idUsuario)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con ID: " + idUsuario));
		
		List<SimpleGrantedAuthority>authorities = usuario.getRol().getAcciones().stream()
                .map(accion -> new SimpleGrantedAuthority(accion.getNombre()))
                .collect(Collectors.toList());
		return new User(
				usuario.getIdUsuario(),
				usuario.getPassword(),
				authorities);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails cargarUsuarioPorId(String idUsuario) {
		return loadUserByUsername(idUsuario);
	}

}
