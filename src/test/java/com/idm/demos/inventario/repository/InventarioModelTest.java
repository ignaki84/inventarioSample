package com.idm.demos.inventario.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.idm.demos.inventario.model.ActivoTecnologico;
import com.idm.demos.inventario.model.CatCategoria;
import com.idm.demos.inventario.model.CatEstadoProducto;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class InventarioModelTest {
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private ActivoTecnologicoRepository activoRepositorio;
	
	@Autowired
	private CatCategoriaRepository categoriaRepositorio;
	
	@Autowired
	private CatEstadoProductoRepository estadoRepositorio;

	@Test
	void test() {
		CatEstadoProducto estado = estadoRepositorio.findById(1)
				.orElseThrow(()->new RuntimeException("No esta el disponible en la BD"));
		
		CatCategoria categoria = categoriaRepositorio.findById(1)
				.orElseThrow(()->new RuntimeException("No hay categorias en la BD"));
		
		assertThat(estado.getDescripcion()).isNotNull();
		assertThat(categoria.getCodPref()).isNotNull();
		
		ActivoTecnologico activo = new ActivoTecnologico();
		activo.setIdProducto("PROD-TEST-001");
        activo.setFolio("FOL-TEST-123");
        activo.setSerie("SN-TEST-987");
        activo.setMarca("Asus");
        activo.setCosto(new BigDecimal("25000.00"));
        activo.setEstadoProducto(estado);
        activo.setCategoria(categoria);
        
        activoRepositorio.save(activo);
        entityManager.flush();
        entityManager.clear();
		
        ActivoTecnologico recuperado = activoRepositorio.findById("PROD-TEST-001").orElse(null);
        assertThat(recuperado).isNotNull();
        assertThat(recuperado.getMarca()).isEqualTo("Asus");
        assertThat(recuperado.getEstadoProducto().getEstatus()).isEqualTo(estado.getEstatus());
        assertThat(recuperado.getCreacion()).isNotNull(); // Validar default timestamp
		
		
	}

}
