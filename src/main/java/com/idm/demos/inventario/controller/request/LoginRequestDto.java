package com.idm.demos.inventario.controller.request;

import lombok.Data;

@Data
public class LoginRequestDto {
	private String idUsuario;
	private String password; //TODO: que viaje encriptado

}
