package com.idm.demos.inventario.utilidades;

public class Utils {
	
	private Utils() {
		throw new UnsupportedOperationException("Clase de utilidades, no se puede instanciar");
	}
	
	public static boolean isDataEmpty(String s) {
		return ( s==null || s.isBlank() ) ? true : false;
	}
	
	
}
