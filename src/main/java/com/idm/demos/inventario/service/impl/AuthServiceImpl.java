package com.idm.demos.inventario.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.f4b6a3.uuid.UuidCreator;
import com.idm.demos.inventario.controller.response.AuthResponseDto;
import com.idm.demos.inventario.model.Sesion;
import com.idm.demos.inventario.model.Usuario;
import com.idm.demos.inventario.repository.SesionRepository;
import com.idm.demos.inventario.repository.UsuarioRepository;
import com.idm.demos.inventario.security.JwtUtil;
import com.idm.demos.inventario.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	
	private final SesionRepository sesionRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    
    @Value("${jwt.expiration-ms}")
    private long expirationMs;

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "sesionesCache", key="#token")
	public UserDetails validarSesionYObtenerUsuario(String token) {
		Sesion sesion = sesionRepository.findByTokenAndEstado(token, 1)
                .orElseThrow(() -> new SecurityException("La sesión no existe o ha sido revocada."));
		
		if (sesion.getExpiracion().isBefore(LocalDateTime.now())) {
            throw new SecurityException("La sesión ha expirado.");
        }
		
		Usuario usuario = sesion.getUsuario();
        List<SimpleGrantedAuthority> authorities = usuario.getRol().getAcciones().stream()
                .map(accion -> new SimpleGrantedAuthority(accion.getNombre()))
                .collect(Collectors.toList());
		
        return new User(usuario.getIdUsuario(), "", authorities);
	}

	@Override
	@Transactional
	public AuthResponseDto crearSesion(String idUsuario, String ip, String userAgent) {
		Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));
		
		String token = jwtUtil.generarToken(idUsuario);
		String refreshToken  = jwtUtil.generarRefreshToken(idUsuario);
		
		Sesion sesion = new Sesion();
        sesion.setIdSesion(UuidCreator.getTimeOrderedEpoch().toString());
        sesion.setUsuario(usuario);
        sesion.setToken(token);
        sesion.setRefreshToken(refreshToken);
        sesion.setIp(ip);
        sesion.setUserAgent(userAgent);
        sesion.setCreacion(LocalDateTime.now());
        sesion.setExpiracion(LocalDateTime.now().plusSeconds(expirationMs/1000));
        sesion.setEstado(1);
        
        sesionRepository.save(sesion);
		
        return new AuthResponseDto(token, refreshToken);
	}
	
	@Override
    @Transactional
    @CacheEvict(value = "sesionesCache", key = "#tokenAnterior")
    public String refrescarSesion(String refreshToken, String tokenAnterior) {
		if (!jwtUtil.validarToken(refreshToken)) {
            throw new SecurityException("Refresh token expirado o inválido. Inicie sesión nuevamente.");
        }
		
		Sesion sesion = sesionRepository.findByRefreshTokenAndEstado(refreshToken, 1)
                .orElseThrow(() -> new SecurityException("Sesión inválida o revocada."));
		
		String nuevoToken = jwtUtil.generarToken(sesion.getUsuario().getIdUsuario());
		sesion.setToken(nuevoToken);
        sesionRepository.save(sesion);
        
        return nuevoToken;
	}

	@Override
	@Transactional
	@CacheEvict(value = "sesionesCache", key="#token")
	public void revocarSesion(String token) {
		sesionRepository.findByTokenAndEstado(token, 1)
			.ifPresent(sesion -> {
	            sesion.setEstado(0);
	            sesionRepository.save(sesion);
	        });
	}

}
