package productos;

import java.util.ArrayList;
import java.util.Date;

public class Comic extends ProductoVenta {
	private int numeroPaginas;
	private String editorial;
	private Date añoPublicacion;
	
	public Comic(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			boolean enOferta, boolean promocionable, int numeroPaginas, String editorial, Date añoPublicacion) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible, enOferta, promocionable);
		this.numeroPaginas = numeroPaginas;
		this.editorial = editorial;
		this.añoPublicacion = añoPublicacion;
	}



}
