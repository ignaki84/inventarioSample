package com.idm.demos.inventario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.idm.demos.inventario.controller.request.LoginRequestDto;
import com.idm.demos.inventario.controller.request.RefreshTokenRequestDto;
import com.idm.demos.inventario.controller.response.ApiResponse;
import com.idm.demos.inventario.controller.response.AuthResponseDto;
import com.idm.demos.inventario.service.AuthService;
import com.idm.demos.inventario.utilidades.Utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(
            @RequestBody LoginRequestDto requestDto,
            HttpServletRequest request) {
    	
    	if( Utils.isDataEmpty(requestDto.getIdUsuario()) 
    			|| Utils.isDataEmpty(requestDto.getPassword()) )
    		throw new IllegalArgumentException("Datos de usuario requeridos.");

        // 1. Validar credenciales reales usando Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getIdUsuario(), requestDto.getPassword())
        );

        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        AuthResponseDto tokens = authService.crearSesion(requestDto.getIdUsuario(), ip, userAgent);

        return ResponseEntity.ok(ApiResponse.success("Inicio de sesión exitoso", tokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDto>> refresh(
            @RequestBody RefreshTokenRequestDto requestDto,
            HttpServletRequest request) {
    	
    	if(Utils.isDataEmpty(requestDto.getRefreshToken()))
    		throw new IllegalArgumentException("Parametros de entrada no validos.");

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Se requiere el token de acceso expirado en el cabecero para validar la rotación.");
        }
        
        String tokenAnterior = authHeader.substring(7);

        String nuevoToken = authService.refrescarSesion(requestDto.getRefreshToken(), tokenAnterior);
        AuthResponseDto response = new AuthResponseDto(nuevoToken, requestDto.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.success("Token renovado con éxito", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.revocarSesion(token);
        }
        
        return ResponseEntity.ok(ApiResponse.success("Sesión cerrada correctamente", null));
    }
}
