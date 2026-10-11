package com.idm.demos.inventario.model;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "CAT_ROL")
public class CatRol {
	@Id
    @Column(name = "FN_IDROL")
    private Integer idRol;

    @Column(name = "FC_NOMBRE", nullable = false)
    private String nombre;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "ROL_ACCION",
        joinColumns = @JoinColumn(name = "FN_IDROL"),
        inverseJoinColumns = @JoinColumn(name = "FN_IDACCION")
    )
    private Set<CatAccion> acciones;
}
