package com.idm.demos.inventario.controller.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
	private int status;
	private String message;
	private LocalDateTime timestamp;
	private T data;
	
	// Métodos estáticos constructores para facilitar su uso en los controladores
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, LocalDateTime.now(), data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, message, LocalDateTime.now(), data);
    }
	
}
