package com.idm.demos.inventario.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.idm.demos.inventario.controller.response.AuthResponseDto;
import com.idm.demos.inventario.model.CatAccion;
import com.idm.demos.inventario.model.CatRol;
import com.idm.demos.inventario.model.Sesion;
import com.idm.demos.inventario.model.Usuario;
import com.idm.demos.inventario.repository.SesionRepository;
import com.idm.demos.inventario.repository.UsuarioRepository;
import com.idm.demos.inventario.security.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

	@Mock private SesionRepository sesionRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private Usuario usuarioSimulado;
    private Sesion sesionSimulada;

    @BeforeEach
    void setUp() {
        // Configurar acciones y rol
        CatAccion accionBusqueda = new CatAccion(); 
        accionBusqueda.setNombre("BUSQUEDA_ACTIVOS");
        
        CatRol rol = new CatRol();
        rol.setAcciones(Set.of(accionBusqueda));

        // Configurar usuario
        usuarioSimulado = new Usuario();
        usuarioSimulado.setIdUsuario("USR-TEST");
        usuarioSimulado.setRol(rol);

        // Configurar sesión activa y vigente
        sesionSimulada = new Sesion();
        sesionSimulada.setIdSesion("UUID-SESION");
        sesionSimulada.setUsuario(usuarioSimulado);
        sesionSimulada.setToken("token.jwt.corto");
        sesionSimulada.setRefreshToken("token.jwt.largo");
        sesionSimulada.setEstado(1);
        sesionSimulada.setExpiracion(LocalDateTime.now().plusHours(1));
    }

    @Test
    void crearSesion_Exito_RetornaTokensYGuardaEnBD() {
    	
        // Arrange
        when(usuarioRepository.findById("USR-TEST")).thenReturn(Optional.of(usuarioSimulado));
        when(jwtUtil.generarToken(any(UserDetails.class))).thenReturn("nuevo.token.corto");
        when(jwtUtil.generarRefreshToken("USR-TEST")).thenReturn("nuevo.token.largo");
        when(sesionRepository.save(any(Sesion.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        AuthResponseDto response = authService.crearSesion("USR-TEST", "192.168.1.1", "Postman");

        // Assert
        assertThat(response.getToken()).isEqualTo("nuevo.token.corto");
        assertThat(response.getRefreshToken()).isEqualTo("nuevo.token.largo");

        // Capturamos el objeto Sesion que se envió a guardar para validar sus campos
        ArgumentCaptor<Sesion> sesionCaptor = ArgumentCaptor.forClass(Sesion.class);
        verify(sesionRepository).save(sesionCaptor.capture());
        
        Sesion sesionGuardada = sesionCaptor.getValue();
        assertThat(sesionGuardada.getIp()).isEqualTo("192.168.1.1");
        assertThat(sesionGuardada.getEstado()).isEqualTo(1); // Activa
        assertThat(sesionGuardada.getUsuario().getIdUsuario()).isEqualTo("USR-TEST");
    }

    @Test
    void crearSesion_UsuarioNoEncontrado_LanzaExcepcion() {
        // Arrange
        when(usuarioRepository.findById("USR-INVALIDO")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.crearSesion("USR-INVALIDO", "127.0.0.1", "Chrome"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
                
        verify(sesionRepository, never()).save(any(Sesion.class));
    }

    @Test
    void validarSesionYObtenerUsuario_Exito_RetornaUserDetails() {
        // Arrange
        when(sesionRepository.findByTokenAndEstado("token.jwt.corto", 1))
                .thenReturn(Optional.of(sesionSimulada));

        // Act
        UserDetails userDetails = authService.validarSesionYObtenerUsuario("token.jwt.corto");

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("USR-TEST");
        // Verifica que la acción del rol se haya mapeado a una autoridad de Spring Security
        assertThat(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("BUSQUEDA_ACTIVOS"))).isTrue();
    }

    @Test
    void validarSesionYObtenerUsuario_SesionExpirada_LanzaExcepcion() {
        // Arrange: Configuramos la sesión como expirada (hace 10 minutos)
        sesionSimulada.setExpiracion(LocalDateTime.now().minusMinutes(10));
        when(sesionRepository.findByTokenAndEstado("token.jwt.corto", 1))
                .thenReturn(Optional.of(sesionSimulada));

        // Act & Assert
        assertThatThrownBy(() -> authService.validarSesionYObtenerUsuario("token.jwt.corto"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("La sesión ha expirado");
    }

    @Test
    void validarSesionYObtenerUsuario_NoEncontradaORevocada_LanzaExcepcion() {
        // Arrange
        when(sesionRepository.findByTokenAndEstado("token.invalido", 1))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.validarSesionYObtenerUsuario("token.invalido"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("La sesión no existe o ha sido revocada");
    }

    @Test
    void refrescarSesion_Exito_ActualizaYRetornaNuevoToken() {
        // Arrange
        String refreshToken = "token.jwt.largo";
        String tokenAnterior = "token.jwt.corto";
        
        when(jwtUtil.validarToken(refreshToken)).thenReturn(true);
        when(sesionRepository.findByRefreshTokenAndEstado(refreshToken, 1))
                .thenReturn(Optional.of(sesionSimulada));
        when(jwtUtil.generarToken(any(UserDetails.class))).thenReturn("nuevo.token.corto.renovado");

        // Act
        String nuevoToken = authService.refrescarSesion(refreshToken, tokenAnterior);

        // Assert
        assertThat(nuevoToken).isEqualTo("nuevo.token.corto.renovado");
        assertThat(sesionSimulada.getToken()).isEqualTo("nuevo.token.corto.renovado");
        verify(sesionRepository).save(sesionSimulada);
    }

    @Test
    void refrescarSesion_RefreshTokenInvalido_LanzaExcepcion() {
        // Arrange
        when(jwtUtil.validarToken("refresh.invalido")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.refrescarSesion("refresh.invalido", "token.viejo"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Refresh token expirado o inválido");
                
        verify(sesionRepository, never()).save(any(Sesion.class));
    }

    @Test
    void revocarSesion_Exito_CambiaEstadoACero() {
        // Arrange
        when(sesionRepository.findByTokenAndEstado("token.jwt.corto", 1))
                .thenReturn(Optional.of(sesionSimulada));

        // Act
        authService.revocarSesion("token.jwt.corto");

        // Assert
        assertThat(sesionSimulada.getEstado()).isEqualTo(0); // Debe estar revocada
        verify(sesionRepository).save(sesionSimulada);
    }
}
