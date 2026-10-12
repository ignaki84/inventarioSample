package com.idm.demos.inventario.service.impl;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import com.idm.demos.inventario.controller.request.ActivoTecUpdateRequestDTO;
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

	private static final String ACCION_ALTA 			= "Registrar activos";
    private static final String ACCION_ACTUALIZACION	= "Actualizar activos";
    private static final String ACCION_BUSQUEDA			= "Consultar informacion";
    private static final String ACCION_ACT_ESTADO		= "Cambio estado";
//    private static final String ACCION_BAJA				= "Baja activos";
    private static final String ACCION_REPORTE			= "Exportar informacion";
    
    @BeforeEach
    void setUp() {
        // Configurar roles y usuario
        CatAccion accionAlta = new CatAccion(); accionAlta.setNombre(ACCION_ALTA);
        CatAccion accionActualizar = new CatAccion(); accionActualizar.setNombre(ACCION_ACTUALIZACION);
        CatAccion accionBusqueda = new CatAccion(); accionBusqueda.setNombre(ACCION_BUSQUEDA);
        CatAccion accionReporte = new CatAccion(); accionReporte.setNombre(ACCION_REPORTE);
        CatAccion accionEdoActualiza = new CatAccion(); accionEdoActualiza.setNombre(ACCION_ACT_ESTADO);

        CatRol rol = new CatRol();
        rol.setAcciones(Set.of(accionAlta, accionActualizar, accionBusqueda,accionReporte,accionEdoActualiza));

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
    }

    @Test
    void registrarActivo_Exito_MapeaEntidadYGeneraIdentificadores() {
        String anioActual = String.valueOf(Year.now().getValue());
        
        when(usuarioRepository.findById("USR-001")).thenReturn(Optional.of(usuarioSimulado));
        when(activoRepository.existsBySerie("SN-12345")).thenReturn(false);
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoriaSimulada));
        when(estadoRepository.findById(1)).thenReturn(Optional.of(estadoSimulado));
        when(activoRepository.obtenerSiguienteValorSecuencia()).thenReturn(Long.valueOf(6));
        
        when(activoRepository.save(any(ActivoTecnologico.class))).thenAnswer(i -> i.getArgument(0));

        ActivoTecnologicoView resultado = activoService.registrarActivo(requestDto, "USR-001");

        assertThat(resultado.getIdProducto()).hasSize(36); // UUID
        assertThat(resultado.getFolio()).isEqualTo("LAP" + anioActual + "000006");
        assertThat(resultado.getSerie()).isEqualTo("SN-12345");
        assertThat(resultado.getEstado()).isEqualTo("Disponible");
        
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

        ActivoTecUpdateRequestDTO reqUpdateDto = new ActivoTecUpdateRequestDTO();
        reqUpdateDto.setSerie("SN-NUEVA");
        reqUpdateDto.setMarca("Lenovo");
        reqUpdateDto.setCosto(new BigDecimal("15000.00"));

        when(usuarioRepository.findById("USR-001")).thenReturn(Optional.of(usuarioSimulado));
        when(activoRepository.findById("UUID-1234")).thenReturn(Optional.of(existente));
        when(activoRepository.existsBySerie("SN-NUEVA")).thenReturn(false);
        when(activoRepository.save(any(ActivoTecnologico.class))).thenAnswer(i -> i.getArgument(0));

        ActivoTecnologicoView resultado = activoService.actualizarActivo("UUID-1234", reqUpdateDto, "USR-001");

        assertThat(resultado.getSerie()).isEqualTo("SN-NUEVA");
        assertThat(resultado.getMarca()).isEqualTo("Lenovo");
        assertThat(resultado.getCosto()).isEqualTo(new BigDecimal("15000.00"));
    }

    @Test
    void cambiarEstado_Exito_RegistraHistorialAutomatico() {
        ActivoTecnologico existente = new ActivoTecnologico();
        existente.setIdProducto("UUID-1234");
        existente.setEstadoProducto(estadoSimulado); // Estado inicial 1
        existente.setCategoria(categoriaSimulada);

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
    
    @SuppressWarnings("unchecked")
	@Test
    void generarReporteAuditoria_Exito_ValidaContenidoZipDecodificado(@TempDir Path tempDir) throws Exception {
        // Arrange: Configuramos un registro simulado para asegurar que sus datos aparezcan en los archivos
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

        // Act: Ejecutamos el método que genera el DTO con el Base64
        ReporteExportacionDto reporte = activoService.generarReporteAuditoria(
                null, null, null, null, null, null, "USR-001");

        // Assert inicial: El DTO no debe ser nulo
        assertThat(reporte).isNotNull();
        assertThat(reporte.getFileName()).isEqualTo("inventario_auditoria.zip");
        assertThat(reporte.getFileBase64()).isNotBlank();

        // 1. Decodificar Base64 a bytes
        byte[] zipBytes = Base64.getDecoder().decode(reporte.getFileBase64());

        // 2. Guardar los bytes en un archivo dentro de la carpeta temporal administrada por JUnit
        Path zipPath = tempDir.resolve("test_auditoria.zip");
        Files.write(zipPath, zipBytes);

        // 3. Abrir el archivo ZIP y validar su contenido
        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
            
            // A) Validar la existencia y contenido del archivo CSV
            ZipEntry csvEntry = zipFile.getEntry("registros.csv");
            assertThat(csvEntry).isNotNull();
            
            try (InputStream is = zipFile.getInputStream(csvEntry)) {
                String csvContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                
                // Verificamos cabeceros y que la información del mock esté plasmada correctamente
                assertThat(csvContent).contains("ID Producto,Folio,Serie,Marca,Costo,Estado,Categoria,Fecha Creacion");
                assertThat(csvContent).contains("UUID-1234,LAP2026000001,SN-12345,Asus,25000.00,Disponible,Laptop");
            }

            // B) Validar la existencia y contenido del archivo TXT de auditoría
            ZipEntry txtEntry = zipFile.getEntry("auditoria.txt");
            assertThat(txtEntry).isNotNull();
            
            try (InputStream is = zipFile.getInputStream(txtEntry)) {
                String txtContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                
                // Verificamos las reglas de negocio (usuario, conteo, fecha)
                assertThat(txtContent).contains("Usuario solicitante: USR-001");
                assertThat(txtContent).contains("Total de registros exportados: 1");
                assertThat(txtContent).contains("Fecha y hora de generación:");
            }
        }
    }
    
    

}
