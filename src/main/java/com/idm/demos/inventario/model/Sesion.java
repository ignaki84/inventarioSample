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
@Table(name = "SESION")
public class Sesion {
	@Id
    @Column(name = "FC_IDSESION", length = 100)
    private String idSesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FC_IDUSUARIO", nullable = false)
    private Usuario usuario;

    @Column(name = "FC_TOKEN", nullable = false, length = 500)
    private String token;

    @Column(name = "FC_REFRESH_TOKEN", length = 500)
    private String refreshToken;

    @Column(name = "FC_IP", length = 45)
    private String ip;

    @Column(name = "FC_USER_AGENT")
    private String userAgent;

    @Column(name = "FD_CREACION", insertable = false, updatable = false)
    private LocalDateTime creacion;

    @Column(name = "FD_EXPIRACION", nullable = false)
    private LocalDateTime expiracion;

    @Column(name = "FN_ESTADO")
    private Integer estado;
}
