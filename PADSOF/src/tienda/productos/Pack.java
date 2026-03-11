package productos;

import java.util.*;

public class Pack extends ProductoVenta {
	private List<ProductoVenta> productosIncluidos;
	private double descuentoPorcentaje; /* número entre 0 y 1 (ej: 0,1 = 10%) */
	private double precioFinal= calcularPrecioFinal();

	
	public Pack(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			boolean enOferta, boolean promocionable, List<ProductoVenta> productosIncluidos, double descuentoPorcentaje) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible, enOferta, promocionable);
		this.productosIncluidos = productosIncluidos;
		this.descuentoPorcentaje = descuentoPorcentaje;
		this.precioFinal = calcularPrecioFinal();
	}

	public void añadirProducto(ProductoVenta p) {
		this.productosIncluidos.add(p);
	}
	
	public double calcularPrecioFinal() {
		double suma = 0;
		for (ProductoVenta p: this.productosIncluidos) {
			suma = suma + p.getPrecioOficial();
		}
		
		return suma * (1-descuentoPorcentaje);
	}
}
