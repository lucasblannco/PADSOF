package productos;

import java.util.*;

public class Comic extends ProductoVenta {
	private int numeroPaginas;
	private String editorial;
	private int añoPublicacion;
	
	public Comic(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			boolean enOferta, boolean promocionable, int numeroPaginas, String editorial, int añoPublicacion) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible, enOferta, promocionable);
		this.numeroPaginas = numeroPaginas;
		this.editorial = editorial;
		this.añoPublicacion = añoPublicacion;
	}



}
