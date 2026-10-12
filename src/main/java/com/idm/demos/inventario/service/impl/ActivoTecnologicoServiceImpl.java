package com.idm.demos.inventario.service.impl;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.f4b6a3.uuid.UuidCreator;
import com.idm.demos.inventario.controller.request.ActivoTecUpdateRequestDTO;
import com.idm.demos.inventario.controller.request.ActivoTecnologicoRequestDto;
import com.idm.demos.inventario.controller.response.ReporteExportacionDto;
import com.idm.demos.inventario.model.ActivoTecnologico;
import com.idm.demos.inventario.model.ActivoTecnologicoView;
import com.idm.demos.inventario.model.CatCategoria;
import com.idm.demos.inventario.model.CatEstadoProducto;
import com.idm.demos.inventario.model.HistorialEstado;
import com.idm.demos.inventario.model.Usuario;
import com.idm.demos.inventario.repository.ActivoTecnologicoRepository;
import com.idm.demos.inventario.repository.ActivoTecnologicoViewRepository;
import com.idm.demos.inventario.repository.CatCategoriaRepository;
import com.idm.demos.inventario.repository.CatEstadoProductoRepository;
import com.idm.demos.inventario.repository.HistorialEstadoRepository;
import com.idm.demos.inventario.repository.UsuarioRepository;
import com.idm.demos.inventario.repository.specification.ActivoTecnologicoSpecification;
import com.idm.demos.inventario.service.ActivoTecnologicoService;
import com.idm.demos.inventario.utilidades.FileCreator;
import com.idm.demos.inventario.utilidades.Utils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivoTecnologicoServiceImpl implements ActivoTecnologicoService {
	
	private final ActivoTecnologicoRepository activoRepo;
	private final ActivoTecnologicoViewRepository vistaRepo;
	private final HistorialEstadoRepository histEdoRepo;
	private final CatEstadoProductoRepository edoRepo;
	private final CatCategoriaRepository catRepo;
	private final UsuarioRepository usuarioRepo;
	
	private static final String NEW_REG		= "Registro de alta inicial";
//	private static final String UPDATE_REG	= "Cambio de datos solicitado mediante APIs";
	private static final String UPDATE_STAT	= "Cambio de estado solicitado mediante APIs";
	
	private static final String ACCION_ALTA 			= "Registrar activos";
    private static final String ACCION_ACTUALIZACION	= "Actualizar activos";
    private static final String ACCION_BUSQUEDA			= "Consultar informacion";
    private static final String ACCION_ACT_ESTADO		= "Cambio estado";
//    private static final String ACCION_BAJA				= "Baja activos";
    private static final String ACCION_REPORTE			= "Exportar informacion";
    
	
    private static final Integer BAJA = 4;
	
	
	@Override
	@Transactional
	public ActivoTecnologicoView registrarActivo(ActivoTecnologicoRequestDto requestDto, String idUsuario) 
	{	
		Usuario usuario = validarPermisos(idUsuario, ACCION_ALTA);
		
		ActivoTecnologico activo = new ActivoTecnologico();
        activo.setSerie(requestDto.getSerie());
        activo.setMarca(requestDto.getMarca());
        activo.setCosto(requestDto.getCosto());
        
        CatCategoria categoria = catRepo.findById(requestDto.getIdCategoria())
                .orElseThrow(() -> new IllegalArgumentException("La categoría del activo es obligatoria."));
        activo.setCategoria(categoria);

        CatEstadoProducto estado = edoRepo.findById(1) //Disponible
                .orElseThrow(() -> new IllegalArgumentException("El estado indicado no existe."));
        activo.setEstadoProducto(estado);
		
        
        validarDatosEntrada(activo, true);

        // Generar folio compuesto
        String folio = generarFolio(activo.getCategoria());
        activo.setFolio(folio);
        
        // Generear el UUID
        String uuid = UuidCreator.getTimeOrderedEpoch().toString();
        activo.setIdProducto(uuid);

        ActivoTecnologico guardado = activoRepo.save(activo);
        registrarHistorial(guardado, null, guardado.getEstadoProducto(), usuario, NEW_REG);
        
        return buildActivoView(guardado);
	}

	@Override
	@Transactional
	public ActivoTecnologicoView actualizarActivo(String idProducto, ActivoTecUpdateRequestDTO requestDto, String idUsuario) 
	{
		validarPermisos(idUsuario, ACCION_ACTUALIZACION);
        
        ActivoTecnologico existente = activoRepo.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Activo no encontrado"));

        if (existente.getEstadoProducto().getEstatus() == BAJA) {
        	throw new IllegalArgumentException("Activo dado de baja.");
        }
        
        // si no es la misma del activo y existe en la BD
        if ( !existente.getSerie().equals(requestDto.getSerie()) 
        		&& activoRepo.existsBySerie(requestDto.getSerie()) ) {
            throw new IllegalArgumentException("El nuevo número de serie ya se encuentra registrado en otro activo.");
        }

        existente.setSerie(requestDto.getSerie());
        existente.setMarca(requestDto.getMarca());
        existente.setCosto(requestDto.getCosto());
        
        validarDatosEntrada(existente, false);
        
        //TODO registra bitacora
        return buildActivoView(activoRepo.save(existente));
	}

	@Override
	@Transactional
	public ActivoTecnologicoView cambiarEstado(String idProducto, Integer idNuevoEstado, String idUsuario) 
	{
		Usuario usuario = validarPermisos(idUsuario, ACCION_ACT_ESTADO);
		
		ActivoTecnologico activo = activoRepo.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Activo no encontrado"));

        CatEstadoProducto estadoAnterior = activo.getEstadoProducto();
        
        if (estadoAnterior.getEstatus()==BAJA) {
        	throw new IllegalArgumentException("Activo dado de baja");
        }
        	
        CatEstadoProducto nuevoEstado = edoRepo.findById(idNuevoEstado)
            .orElseThrow(() -> new IllegalArgumentException("Estado no válido"));
        
        activo.setEstadoProducto(nuevoEstado);
        activoRepo.save(activo);

        registrarHistorial(activo, estadoAnterior, nuevoEstado, usuario, UPDATE_STAT);
        return buildActivoView(activo);
	}

	@Override
    @Transactional(readOnly = true)
    public Page<ActivoTecnologicoView> buscarActivos(String serie, String marca, String estado, String categoria,
    		BigDecimal costoMin, BigDecimal costoMax, Pageable pageable, String idUsuario) {
        validarPermisos(idUsuario, ACCION_BUSQUEDA);
        Specification<ActivoTecnologicoView> spec = ActivoTecnologicoSpecification.buildFilter(serie, marca, estado, categoria, costoMin, costoMax);
        return vistaRepo.findAll(spec, pageable);
    }
	
	@Override
    @Transactional(readOnly = true)
	public ReporteExportacionDto generarReporteAuditoria(String serie, String marca, String estado, String categoria,
			BigDecimal costoMin, BigDecimal costoMax, String idUsuario){
		Usuario usuario = validarPermisos(idUsuario, ACCION_REPORTE);
		
		Specification<ActivoTecnologicoView> spec = ActivoTecnologicoSpecification.buildFilter(serie, marca, estado, categoria, costoMin, costoMax);
        List<ActivoTecnologicoView> activos = vistaRepo.findAll(spec);

        String base64File = new FileCreator().zipAuditFile(activos, usuario.getIdUsuario());
        String fileName = "inventario_auditoria.zip";

        return new ReporteExportacionDto(fileName, base64File);
	}
	
	private String generarFolio(CatCategoria categoria) {
        String prefijo = categoria.getCodPref();
        String anio = String.valueOf(Year.now().getValue());
        Long consecutivo = activoRepo.obtenerSiguienteValorSecuencia()%1000000;
        
        // Asegura formato de 6 dígitos rellenados con ceros a la izquierda
        return String.format("%s%s%06d", prefijo, anio, consecutivo);
    }
	
	private void registrarHistorial(ActivoTecnologico activo, CatEstadoProducto estadoAnterior, CatEstadoProducto estadoNuevo,
			Usuario usuarioModifica, String comentarios)
	{
        HistorialEstado historial = new HistorialEstado();
        historial.setActivoTecnologico(activo);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(estadoNuevo);
        historial.setUsuario(usuarioModifica);
        historial.setComentarios(comentarios);
        
        histEdoRepo.save(historial);
    }
	
	
	private Usuario validarPermisos(String idUsuario, String accionRequerida) {
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new SecurityException("Usuario no identificado en el sistema."));

        boolean tienePermiso = usuario.getRol().getAcciones().stream()
                .anyMatch(accion -> accion.getNombre().equalsIgnoreCase(accionRequerida));

        if (!tienePermiso) {
            throw new SecurityException("El rol del usuario no tiene privilegios para ejecutar la acción: " + accionRequerida);
        }
        return usuario;
    }
	
	private ActivoTecnologicoView buildActivoView(ActivoTecnologico activo) {
		ActivoTecnologicoView result = new ActivoTecnologicoView();
		result.setCategoria(activo.getCategoria().getNombre());
		result.setCodPref(activo.getCategoria().getCodPref());
		result.setCosto(activo.getCosto());
		result.setCreacion(activo.getCreacion());
		result.setEstado(activo.getEstadoProducto().getDescripcion());
		result.setFolio(activo.getFolio());
		result.setIdProducto(activo.getIdProducto());
		result.setMarca(activo.getMarca());
		result.setSerie(activo.getSerie());
		
		return result;
	}
	
	private void validarDatosEntrada(ActivoTecnologico activo, boolean esAlta) 
	{
		if ( Utils.isDataEmpty(activo.getSerie()) || activo.getSerie().length() > 20 ) { 
			throw new IllegalArgumentException("El número de serie es obligatorio max 20 caracteres."); 
		}
		
		if (esAlta && activoRepo.existsBySerie(activo.getSerie())) {
			throw new IllegalArgumentException("El número de serie ingresado ya existe en la base de datos.");
        }

		if (activo.getCosto() == null || activo.getCosto().compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("El costo es obligatorio y no puede tener un valor negativo.");
        }
		
		if ( Utils.isDataEmpty(activo.getMarca()) || activo.getMarca().length() > 100 ) {
			throw new IllegalArgumentException("La marca del activo es obligatoria max 100 caracteres.");
		}
	}

}
