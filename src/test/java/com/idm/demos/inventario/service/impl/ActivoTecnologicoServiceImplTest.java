package com.idm.demos.inventario.service.impl;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import com.idm.demos.inventario.controller.request.ActivoTecnologicoRequestDto;
import com.idm.demos.inventario.controller.response.ReporteExportacionDto;
import com.idm.demos.inventario.model.ActivoTecnologico;
import com.idm.demos.inventario.model.ActivoTecnologicoView;
import com.idm.demos.inventario.model.CatAccion;
import com.idm.demos.inventario.model.CatCategoria;
import com.idm.demos.inventario.model.CatEstadoProducto;
import com.idm.demos.inventario.model.CatRol;
import com.idm.demos.inventario.model.HistorialEstado;
import com.idm.demos.inventario.model.Usuario;
import com.idm.demos.inventario.repository.ActivoTecnologicoRepository;
import com.idm.demos.inventario.repository.ActivoTecnologicoViewRepository;
import com.idm.demos.inventario.repository.CatCategoriaRepository;
import com.idm.demos.inventario.repository.CatEstadoProductoRepository;
import com.idm.demos.inventario.repository.HistorialEstadoRepository;
import com.idm.demos.inventario.repository.UsuarioRepository;


@ExtendWith(MockitoExtension.class)
class ActivoTecnologicoServiceImplTest {
	
	@Mock private ActivoTecnologicoRepository activoRepository;
    @Mock private ActivoTecnologicoViewRepository vistaRepository;
    @Mock private HistorialEstadoRepository historialRepository;
    @Mock private CatCategoriaRepository categoriaRepository;
    @Mock private CatEstadoProductoRepository estadoRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ActivoTecnologicoServiceImpl activoService;

    private Usuario usuarioSimulado;
    private CatCategoria categoriaSimulada;
    private CatEstadoProducto estadoSimulado;
    private ActivoTecnologicoRequestDto requestDto;

    @BeforeEach
    void setUp() {
        // Configurar roles y usuario
        CatAccion accionAlta = new CatAccion(); accionAlta.setNombre("ALTA_ACTIVOS");
        CatAccion accionActualizar = new CatAccion(); accionActualizar.setNombre("ACTUALIZAR_ACTIVOS");
        CatAccion accionBusqueda = new CatAccion(); accionBusqueda.setNombre("BUSQUEDA_ACTIVOS");

        CatRol rol = new CatRol();
        rol.setAcciones(Set.of(accionAlta, accionActualizar, accionBusqueda));

        usuarioSimulado = new Usuario();
        usuarioSimulado.setIdUsuario("USR-001");
        usuarioSimulado.setEmail("test@empresa.com");
        usuarioSimulado.setRol(rol);

        // Configurar catálogos
        categoriaSimulada = new CatCategoria();
        categoriaSimulada.setIdCategoria(1);
        categoriaSimulada.setCodPref("LAP");

        estadoSimulado = new CatEstadoProducto();
        estadoSimulado.setEstatus(1);
        estadoSimulado.setDescripcion("Disponible");

        // Configurar DTO de entrada
        requestDto = new ActivoTecnologicoRequestDto();
        requestDto.setSerie("SN-12345");
        requestDto.setMarca("Asus");
        requestDto.setCosto(new BigDecimal("25000.00"));
        requestDto.setIdCategoria(1);
        requestDto.setIdEstatus(1);
    }

    @Test
    void registrarActivo_Exito_MapeaEntidadYGeneraIdentificadores() {
        String anioActual = String.valueOf(Year.now().getValue());
        
        when(usuarioRepository.findById("USR-001")).thenReturn(Optional.of(usuarioSimulado));
        when(activoRepository.existsBySerie("SN-12345")).thenReturn(false);
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoriaSimulada));
        when(estadoRepository.findById(1)).thenReturn(Optional.of(estadoSimulado));
        when(activoRepository.findMaxFolioByCategoriaAndAnio(1, "LAP", anioActual))
                .thenReturn("LAP" + anioActual + "000005");
        
        when(activoRepository.save(any(ActivoTecnologico.class))).thenAnswer(i -> i.getArgument(0));

        ActivoTecnologico resultado = activoService.registrarActivo(requestDto, "USR-001");

        assertThat(resultado.getIdProducto()).hasSize(36); // UUID
        assertThat(resultado.getFolio()).isEqualTo("LAP" + anioActual + "000006");
        assertThat(resultado.getSerie()).isEqualTo("SN-12345");
        assertThat(resultado.getEstadoProducto()).isEqualTo(estadoSimulado);
        
        verify(activoRepository).save(any(ActivoTecnologico.class));
        verify(historialRepository).save(any(HistorialEstado.class));
    }

    @Test
    void actualizarActivo_Exito_ActualizaCamposPermitidos() {
    	CatEstadoProducto estadoNew = new CatEstadoProducto();
		estadoNew.setEstatus(2);
		
        ActivoTecnologico existente = new ActivoTecnologico();
        existente.setIdProducto("UUID-1234");
        existente.setSerie("SN-ANTIGUA");
        existente.setMarca("Dell");
        existente.setCosto(new BigDecimal("10000.00"));
        existente.setCategoria(categoriaSimulada);
        existente.setEstadoProducto(estadoNew);

        requestDto.setSerie("SN-NUEVA");
        requestDto.setMarca("Lenovo");
        requestDto.setCosto(new BigDecimal("15000.00"));

        when(usuarioRepository.findById("USR-001")).thenReturn(Optional.of(usuarioSimulado));
        when(activoRepository.findById("UUID-1234")).thenReturn(Optional.of(existente));
        when(activoRepository.existsBySerie("SN-NUEVA")).thenReturn(false);
        when(activoRepository.save(any(ActivoTecnologico.class))).thenAnswer(i -> i.getArgument(0));

        ActivoTecnologico resultado = activoService.actualizarActivo("UUID-1234", requestDto, "USR-001");

        assertThat(resultado.getSerie()).isEqualTo("SN-NUEVA");
        assertThat(resultado.getMarca()).isEqualTo("Lenovo");
        assertThat(resultado.getCosto()).isEqualTo(new BigDecimal("15000.00"));
    }

    @Test
    void cambiarEstado_Exito_RegistraHistorialAutomatico() {
        ActivoTecnologico existente = new ActivoTecnologico();
        existente.setIdProducto("UUID-1234");
        existente.setEstadoProducto(estadoSimulado); // Estado inicial 1

        CatEstadoProducto nuevoEstado = new CatEstadoProducto();
        nuevoEstado.setEstatus(2); // Estado Asignado

        when(usuarioRepository.findById("USR-001")).thenReturn(Optional.of(usuarioSimulado));
        when(activoRepository.findById("UUID-1234")).thenReturn(Optional.of(existente));
        when(estadoRepository.findById(2)).thenReturn(Optional.of(nuevoEstado));

        activoService.cambiarEstado("UUID-1234", 2, "USR-001");

        verify(activoRepository).save(existente);
        verify(historialRepository).save(any(HistorialEstado.class));
        assertThat(existente.getEstadoProducto().getEstatus()).isEqualTo(2);
    }

    @SuppressWarnings("unchecked")
	@Test
    void generarReporteAuditoria_Exito_RetornaZipBase64() {
        ActivoTecnologicoView vista = new ActivoTecnologicoView();
        vista.setIdProducto("UUID-1234");
        vista.setFolio("LAP2026000001");
        vista.setSerie("SN-12345");
        vista.setMarca("Asus");
        vista.setCosto(new BigDecimal("25000.00"));
        vista.setEstado("Disponible");
        vista.setCategoria("Laptop");
        vista.setCreacion(LocalDateTime.now());

        when(usuarioRepository.findById("USR-001")).thenReturn(Optional.of(usuarioSimulado));
        when(vistaRepository.findAll(any(Specification.class))).thenReturn(List.of(vista));

        ReporteExportacionDto reporte = activoService.generarReporteAuditoria(
                null, null, null, null, null, null, "USR-001");

        assertThat(reporte).isNotNull();
        assertThat(reporte.getFileName()).isEqualTo("inventario_auditoria.zip");
        assertThat(reporte.getFileBase64()).isNotBlank();
        // Verificar que sea una cadena Base64 válida
        assertThat(reporte.getFileBase64().matches("^[a-zA-Z0-9+/]*={0,2}$")).isTrue();
    }

}
