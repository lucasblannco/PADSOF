package productos;

import java.util.*;

public class Stock {

	private ArrayList<ProductoVenta> productos;

	public Stock() {
		this.productos = new ArrayList<>();
	}

	public boolean añadirProducto(ProductoVenta producto) {
		if (producto == null || this.productos.contains(producto)) {
			return false;
		}
		return this.productos.add(producto);
	}

	public boolean eliminarProducto(ProductoVenta producto) {
		if (producto == null) {
			return false;
		}
		return this.productos.remove(producto);
	}

	public ArrayList<ProductoVenta> getProductos() {
		return new ArrayList<>(this.productos);
	}

	// De menos a más stock
	public ArrayList<ProductoVenta> getProductosOrdenadosStockAsc() {
		ArrayList<ProductoVenta> copia = new ArrayList<>(this.productos);

		Collections.sort(copia, Comparator.comparingInt(ProductoVenta::getStockDisponible));

		return copia;
	}

	// De más a menos stock
	public ArrayList<ProductoVenta> getProductosOrdenadosStockDesc() {
		ArrayList<ProductoVenta> copia = new ArrayList<>(this.productos);

		Collections.sort(copia, Comparator.comparingInt(ProductoVenta::getStockDisponible).reversed());

		return copia;
	}

	// Productos sin unidades de stock
	public ArrayList<ProductoVenta> getProductosSinStock() {
		ArrayList<ProductoVenta> resultado = new ArrayList<>();
		for (ProductoVenta p : this.productos) {
			if (p.getStockDisponible() == 0) {
				resultado.add(p);
			}
		}
		return resultado;
	}

	// Busqueda de productos por id
	public ProductoVenta buscarProductoPorId(String id) {
		if (id == null) {
			return null;
		}

		for (ProductoVenta p : this.productos) {
			if (p.getId().equals(id)) {
				return p;
			}
		}

		return null;
	}
}

/* ============ ESTA CLASE SOBRA ============== */