package productos;

import Excepcion.ProductoInvalidoException;

public class LineaPack {
	private ProductoVenta producto;
	private int unidades;

	public LineaPack(ProductoVenta producto, int unidades) {
		if (producto == null) {
			throw new ProductoInvalidoException("El producto de la línea del pack no puede ser null.");
		}
		if (unidades <= 0) {
			throw new ProductoInvalidoException("Las unidades de la línea del pack deben ser mayores que 0.");
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
		return producto.getPrecioOficial() * unidades;
	}

	public void setUnidades(int nuevasUnidades) {
		if (nuevasUnidades <= 0) {
			throw new ProductoInvalidoException("Las nuevas unidades deben ser mayores que 0.");
		}
		this.unidades = nuevasUnidades;
	}
}
