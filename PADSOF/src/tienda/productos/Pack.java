package productos;

import java.util.*;

public class Pack extends ProductoVenta {
	private ArrayList<ProductoVenta> productosIncluidos;
	private double descuentoPorcentaje; /* número entre 0 y 1 (ej: 0,1 = 10%) (se corrige si mete 10) */

	public Pack(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			boolean enOferta, boolean promocionable, double descuentoPorcentaje) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible);
		this.productosIncluidos = new ArrayList<ProductoVenta>();

		if (descuentoPorcentaje > 1) {
			descuentoPorcentaje /= 100;
		}
		if (descuentoPorcentaje < 0) {
			descuentoPorcentaje = 0;
		}
		if (descuentoPorcentaje > 1) {
			descuentoPorcentaje = 1;
		}
		this.descuentoPorcentaje = descuentoPorcentaje;
	}

	public boolean addProducto(ProductoVenta p) {
		if (p == null)
			return false;
		this.productosIncluidos.add(p);
		return true;
	}

	public boolean eliminarProducto(ProductoVenta p) {
		if (p == null) {
			return false;
		}
		return this.productosIncluidos.remove(p);
	}

	public double calcularPrecioFinal() {
		double suma = 0;
		for (ProductoVenta p : this.productosIncluidos) {
			suma += p.getPrecioVenta();
		}

		return suma * (1 - descuentoPorcentaje);
	}

	@Override
	public double getPrecioVenta() {
		return calcularPrecioFinal();
	}

	@Override
	public String toString() {
		String s = null;

		for (ProductoVenta pv : this.productosIncluidos) {
			s = s + pv.toString() + "\n";
		}

		return super.toString() + "/n" + s;
	}

}
