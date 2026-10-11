package com.idm.demos.inventario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "CAT_CATEGORIA")
public class CatCategoria {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FN_IDCATEGORIA")
    private Integer idCategoria;

    @Column(name = "FC_CODPREF", nullable = false, length = 5)
    private String codPref;

    @Column(name = "FC_NOMBRE", nullable = false)
    private String nombre;
}
