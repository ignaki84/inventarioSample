package com.idm.demos.inventario.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "USUARIO")
public class Usuario {
	@Id
    @Column(name = "FC_IDUSUARIO", nullable = false, length = 20, unique = true)
    private String idUsuario;

    @Column(name = "FC_NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "FC_EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "FC_PASSWORD", nullable = false)
    private String password;

    @Column(name = "FD_CREACION", insertable = false, updatable = false)
    private LocalDateTime creacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FN_IDROL", nullable = false)
    private CatRol rol;
}
