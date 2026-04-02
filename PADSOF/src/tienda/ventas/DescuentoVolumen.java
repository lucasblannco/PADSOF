package ventas;

import java.time.LocalDateTime;

public class DescuentoVolumen extends Descuento {
	private double umbralMinimo;
	private double porcentaje;

	public DescuentoVolumen(String nombre, LocalDateTime inicio, LocalDateTime fin, double umbralMinimo,
			double porcentaje) {
		super(nombre, inicio, fin);
		this.umbralMinimo = umbralMinimo;
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
		double subtotal = carrito.calcularSubtotal();
		if (subtotal < umbralMinimo)
			return subtotal;
		return subtotal * (1 - porcentaje);
	}

	public double getUmbralMinimo() {
		return umbralMinimo;
	}

	public double getPorcentaje() {
		return porcentaje;
	}
}
