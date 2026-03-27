package productos;

import java.util.*;

public class Categoria {
	private String nombre;
	private String descripcion;
	private ArrayList<ProductoVenta> productos;

	public Categoria(String nombre, String descripcion) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.productos = new ArrayList<>();
		;
	}

	public boolean addProducto(ProductoVenta p) {
		if (p == null) {
			return false;
		}
		if (this.productos.contains(p)) {
			System.out.println("La categoria " + this.getNombre() + " ya contiene al producto " + p.getNombre()
					+ " . No se puede añadir.");
			return false;
		}

		this.productos.add(p);

		p.addCategoriaInterno(this);

		return true;
	}

	public boolean deleteProducto(ProductoVenta p) {
		if (p == null) {
			return false;
		}
		if (!this.productos.contains(p)) {
			System.out.println("La categoria " + this.getNombre() + " no contiene al producto " + p.getNombre()
					+ ". No se puede eliminar");
			return false;
		}

		this.productos.remove(p);

		p.deleteCategoriaInterno(this);
		return true;
	}

	protected boolean addProductoInterno(ProductoVenta p) {
		if (p == null || this.productos.contains(p)) {
			return false;
		}
		this.productos.add(p);
		return true;
	}

	protected boolean deleteProductoInterno(ProductoVenta p) {
		if (p == null || !this.productos.contains(p)) {
			return false;
		}
		this.productos.remove(p);
		return true;
	}

	@Override
	public String toString() {
		return "Categoria [nombre=" + nombre + ", descripcion=" + descripcion + "]";
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public ArrayList<ProductoVenta> getProductos() {
		return productos;
	}

}
