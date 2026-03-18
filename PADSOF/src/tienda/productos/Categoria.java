package productos;

import java.util.*;

public class Categoria {
	private String nombre;
	private String descripcion;
	private ArrayList<Producto> productos;

	public Categoria(String nombre, String descripcion) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.productos = new ArrayList<>();
		;
	}

	public boolean addProducto(Producto p) {
		if (p == null) {
			return false;
		}
		if (this.productos.contains(p)) {
			return false;
		}

		this.productos.add(p);

		if (p instanceof ProductoVenta) {
			((ProductoVenta) p).addCategoriaInterno(this);
		}

		return true;
	}

	public boolean deleteProducto(Producto p) {
		if (p == null) {
			return false;
		}
		if (!this.productos.contains(p)) {
			return false;
		}

		this.productos.remove(p);

		if (p instanceof ProductoVenta) {
			((ProductoVenta) p).deleteCategoriaInterno(this);
		}

		return true;
	}

	protected boolean addProductoInterno(Producto p) {
		if (p == null || this.productos.contains(p)) {
			return false;
		}
		this.productos.add(p);
		return true;
	}

	protected boolean deleteProductoInterno(Producto p) {
		if (p == null || !this.productos.contains(p)) {
			return false;
		}
		this.productos.remove(p);
		return true;
	}
}
