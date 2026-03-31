package ventas;

import productos.ProductoVenta;

public class LineaCarrito {
	private ProductoVenta producto;
	private int cantidad;

	public LineaCarrito(ProductoVenta producto, int cantidad) {
		if (producto == null || cantidad <= 0) {
			throw new IllegalArgumentException("Argumentos de linea carrito inválidos");
		}
		this.producto = producto;
		this.cantidad = cantidad;
	}

	public boolean productoPertence(ProductoVenta p) {
		if (p == null) {
			return false;
		}
		if (producto.getId().equals(p.getId())) {
			return true;
		}
		return false;
	}

	public void setCantidad(int cantidad) {
		if (cantidad < 0) {
			return;
		}
		this.cantidad = cantidad;
	}

	public int getCantidad() {
		return this.cantidad;
	}

	public ProductoVenta getProducto() {
		return this.producto;
	}

	public double getSubtotal() {
		return this.producto.getPrecioOficial() * this.cantidad;
	}

}
