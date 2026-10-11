package com.idm.demos.inventario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="CAT_ESTADO_PRODUCTO")
public class CatEstadoProducto {
	@Id
	@Column(name="FN_ESTATUS")
	private Integer estatus;
	
	@Column(name="FC_DESCRIPCION")
	private String descripcion;

}
