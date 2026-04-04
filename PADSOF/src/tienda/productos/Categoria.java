package productos;

import java.util.*;
import Excepcion.ProductoYaEnCategoriaException;

public class Categoria {
	private String nombre;
	private String descripcion;
	private ArrayList<ProductoVenta> productos;

	public Categoria(String nombre, String descripcion) {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.productos = new ArrayList<>();
	}

	public boolean addProducto(ProductoVenta p) {
		if (p == null) {
			return false;
		}
		if (this.productos.contains(p)) {
			throw new ProductoYaEnCategoriaException(
					"La categoria " + this.getNombre() + " ya contiene al producto " + p.getNombre() + ".");
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
		String textoProductos = "";

		if (this.productos.isEmpty()) {
			textoProductos = "ninguno";
		} else {
			for (ProductoVenta p : this.productos) {
				textoProductos += "[" + p.getId() + " " + p.getNombre() + "], ";
			}
			textoProductos = textoProductos.substring(0, textoProductos.length() - 2);
		}

		return "| Nombre: " + this.nombre + " | Descripción: " + this.descripcion + " | Productos: " + textoProductos
				+ " |";
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
