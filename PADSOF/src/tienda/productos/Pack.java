package productos;

import java.util.List;
import java.util.ArrayList;

public class Pack extends ProductoVenta {
	private List<ProductoVenta> productosIncluidos;
	private double descuentoPorcentaje; /* número entre 0 y 1 (ej: 0,1 = 10%) */

	public void añadirProducto(ProductoVenta p) {
		this.productosIncluidos.add(p);
	}

	@Override
	public double calcularPrecioFinal() {
		double sumaPrecios = 0;
		for (ProductoVenta p : productosIncluidos) {
			sumaPrecios += p.calcularPrecioFinal();// tenemos en cuenta el precio con descuento
			// sumaPrecios += p.getPrecioOficial(); // tenemos en cuenta el precio oficial
		}

		return sumaPrecios * (1 - descuentoPorcentaje);
	}

	// --- GETTERS Y SETTERS ---
	public List<ProductoVenta> getProductosIncluidos() {
		return productosIncluidos;
	}

	public void setProductosIncluidos(List<ProductoVenta> productosIncluidos) {
		this.productosIncluidos = productosIncluidos;
	}

	public double getDescuentoPorcentaje() {
		return descuentoPorcentaje;
	}

	public void setDescuentoPorcentaje(double descuentoPorcentaje) {
		this.descuentoPorcentaje = descuentoPorcentaje;
	}
}
