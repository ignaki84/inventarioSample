package com.idm.demos.inventario.repository.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.idm.demos.inventario.model.ActivoTecnologicoView;

import jakarta.persistence.criteria.Predicate;

public class ActivoTecnologicoSpecification 
{
	
	public static Specification<ActivoTecnologicoView> buildFilter(
			String serie, String marca, String estado, String categoria, 
            BigDecimal costoMin, BigDecimal costoMax)
	{
		// agregar likes para consultas de elementos similares y rango de costos
		// TODO: usar la clase Utils??
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if(serie!=null && !serie.isBlank()) {	
				predicates.add(cb.like(root.get("serie"), "%"+serie+"%"));
			}
			if(marca!=null && !marca.isBlank()) {
				predicates.add(cb.like(root.get("marca"), "%"+marca+"%"));
			}
			if (estado != null && !estado.isBlank()) {
                predicates.add(cb.equal(root.get("estado"), estado));
            }
            if (categoria != null && !categoria.isBlank()) {
                predicates.add(cb.equal(root.get("categoria"), categoria));
            }
            if (costoMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("costo"), costoMin));
            }
            if (costoMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("costo"), costoMax));
            }
            
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

}
