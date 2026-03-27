package ventas;

import java.time.LocalDateTime;
import productos.ProductoVenta;

public class Regalo extends Descuento {
	private double umbral;
	private ProductoVenta producto;

	public Regalo(String nombre, LocalDateTime inicio, LocalDateTime fin, double umbral, ProductoVenta producto) {
		super(nombre, inicio, fin);
		this.umbral = umbral;
		this.producto = producto;
	}

	public boolean aplicaRegalo(Carrito carrito) {
		return estaActivo() && carrito.calcularSubtotal() >= umbral && producto != null
				&& producto.getStockDisponible() > 0;
	}

	public ProductoVenta getProductoRegalo() {
		return producto;
	}

	@Override
	public double aplicarDescuento(Carrito carrito) {
		return carrito.calcularSubtotal();
	}
}