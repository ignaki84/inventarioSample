package com.idm.demos.inventario.service;

import org.springframework.security.core.userdetails.UserDetails;

import com.idm.demos.inventario.controller.response.AuthResponseDto;

public interface AuthService {
	UserDetails validarSesionYObtenerUsuario(String token);
	
	AuthResponseDto crearSesion(String idUsuario, String ip, String userAgent);
	public String refrescarSesion(String refreshToken, String tokenAnterior);
    void revocarSesion(String token);
}
