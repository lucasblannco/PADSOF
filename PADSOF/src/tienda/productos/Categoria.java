package productos;

import java.util.*;

public class Categoria {
	private String nombre;
	private String descripcion;
	private ArrayList<Producto> productos;

	public boolean addProducto(Producto p) {
		if (p == null) {
			return false;
		}

		this.productos.add(p);
		return true;
	}
	
	public boolean deleteProducto(Producto p) {
		if(p== null) {
			return false;
		}
this.productos.remove(p);
return true;
	}
}
