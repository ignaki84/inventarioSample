package com.idm.demos.inventario.repository.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.idm.demos.inventario.model.ActivoTecnologicoView;
import com.idm.demos.inventario.utilidades.Utils;

import jakarta.persistence.criteria.Predicate;

public class ActivoTecnologicoSpecification 
{
	
	public static Specification<ActivoTecnologicoView> buildFilter(
			String serie, String marca, String estado, String categoria, 
            BigDecimal costoMin, BigDecimal costoMax)
	{
		// agregar likes para consultas de elementos similares y rango de costos
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if( !Utils.isDataEmpty(serie) ) {	
				predicates.add(cb.like(root.get("serie"), "%"+serie+"%"));
			}
			if( !Utils.isDataEmpty(marca) ) {
				predicates.add(cb.like(root.get("marca"), "%"+marca+"%"));
			}
			if ( !Utils.isDataEmpty(estado) ) {
                predicates.add(cb.equal(root.get("estado"), estado));
            }
            if ( !Utils.isDataEmpty(categoria) ) {
                predicates.add(cb.equal(root.get("categoria"), categoria));
            }
            if (costoMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("costo"), costoMin));
            }
            if (costoMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("costo"), costoMax));
            }
            
//            if( predicates.size() > 0 )
            	return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

}
