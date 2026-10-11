package com.idm.demos.inventario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "CAT_ACCION")
public class CatAccion {
	@Id
    @Column(name = "FN_IDACCION")
    private Integer idAccion;

    @Column(name = "FC_NOMBRE", nullable = false)
    private String nombre;
}
