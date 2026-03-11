package productos;

import java.util.ArrayList;
import java.util.Date;

public class Comic extends ProductoVenta {
    private int numeroPaginas;
    private String editorial;
    private Date añoPublicacion;
    
	public Comic(String id, String nombre, String descripcion, String imagenRuta, double precioOficial,
			int stockDisponible, boolean enOferta, ArrayList<Reseña> reseñas, boolean promocionable, int numeroPaginas,
			String editorial, Date añoPublicacion) {
		super(id, nombre, descripcion, imagenRuta, precioOficial, stockDisponible, enOferta, reseñas, promocionable);
		this.numeroPaginas = numeroPaginas;
		this.editorial = editorial;
		this.añoPublicacion = añoPublicacion;
	}
    
	
}
