package productos;

public class LineaPack {
	private ProductoVenta producto;
	private int unidades;

	public LineaPack(ProductoVenta producto, int unidades) {
		if (producto == null || unidades <= 0) {
			throw new IllegalArgumentException("Producto no null y unidades postitivas");
		}
		this.producto = producto;
		this.unidades = unidades;
	}

	public ProductoVenta getProducto() {
		return producto;
	}

	public int getUnidades() {
		return unidades;
	}

	public double getSubtotal() {
		return producto.getPrecioVenta() * unidades;
	}

	public void setUnidades(int nuevasUnidades) {
		if (nuevasUnidades <= 0)
			return;
		this.unidades = nuevasUnidades;

	}
}
