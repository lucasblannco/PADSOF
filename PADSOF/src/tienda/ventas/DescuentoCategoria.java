package ventas;

import java.time.LocalDateTime;
import productos.Categoria;

public class DescuentoCategoria extends Descuento {
	private Categoria categoria;
	private double porcentaje;

	public DescuentoCategoria(String nombre, LocalDateTime inicio, LocalDateTime fin, Categoria categoria,
			double porcentaje) {
		super(nombre, inicio, fin);
		this.categoria = categoria;
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

			if (linea.getProducto().getCategorias().contains(categoria)) {
				subtotalLinea = subtotalLinea * (1 - porcentaje);
			}

			total += subtotalLinea;
		}
		return total;
	}
}
