package ventas;

import java.time.LocalDateTime;

public class DescuentoCantidad extends Descuento {
	private int cantidadMinima;
	private double porcentaje;

	public DescuentoCantidad(String nombre, LocalDateTime inicio, LocalDateTime fin, int cantidadMinima,
			double porcentaje) {
		super(nombre, inicio, fin);
		this.cantidadMinima = cantidadMinima;
		if (porcentaje > 1)
			porcentaje /= 100;
		if (porcentaje < 0)
			porcentaje = 0;
		this.porcentaje = porcentaje;
	}

	@Override
	public double aplicarDescuento(Carrito carrito) {
		if (!estaActivo())
			return carrito.calcularSubtotal();

		double total = 0;
		for (LineaCarrito linea : carrito.getLineas()) {
			double subtotalLinea = linea.getSubtotal();

			if (linea.getCantidad() >= cantidadMinima) {
				int unidadesConDescuento = linea.getCantidad() - cantidadMinima;
				double precioUnitario = linea.getProducto().getPrecioOficial();
				double descuento = unidadesConDescuento * precioUnitario * porcentaje;
				subtotalLinea -= descuento;
			}

			total += subtotalLinea;
		}
		return total;
	}

	public int getCantidadMinima() {
		return cantidadMinima;
	}
}
