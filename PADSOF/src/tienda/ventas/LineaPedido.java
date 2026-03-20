package ventas;

import productos.ProductoVenta;

public class LineaPedido {
	private final ProductoVenta producto;
	private final int cantidad;
	private final double precioVenta; // Precio en el momento de la compra

	public LineaPedido(ProductoVenta producto, int cantidad, double precioVenta) {
		this.producto = producto;
		this.cantidad = cantidad;
		this.precioVenta = precioVenta;
	}

	public double getSubtotal() {
		return this.precioVenta * cantidad;
	}

	public boolean productoPertenece(ProductoVenta p) {
		if (p != null && producto.getId().equals(p.getId())) {
			return true;
		}
		return false;
	}

	public ProductoVenta getProducto() {
		return this.producto;
	}

	public int getCantidad() {
		return cantidad;
	}
	
	public double getPrecioVenta() {
		return this.precioVenta;
	}
}
