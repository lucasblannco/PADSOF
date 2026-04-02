package tienda;

import productos.EstadoProducto;
import productos.Producto2Mano;

public class FiltroSegundaMano {

	private double valorMinimo;
	private double valorMaximo;

	// El enum EstadoProducto esta ordenado de mejor a peor estado:
	// PERFECTO(0) > MUY_BUENO(1) > USO_LIGERO(2) > USO_EVIDENTE(3) > MUY_USADO(4) > DAÑADO(5) > NO_ACEPTADO(6)
	// estadoMinimo indica la calidad minima aceptada: un producto cumple el filtro
	// si su estado tiene ordinal <= estadoMinimo.ordinal() (es igual o mejor calidad).
	private EstadoProducto estadoMinimo;

	public FiltroSegundaMano() {
		resetear();
	}

	/**
	 * Devuelve true si el producto cumple todos los criterios:
	 * - es visible y no esta bloqueado
	 * - tiene valoracion (no null)
	 * - precio de tasacion dentro del rango [valorMinimo, valorMaximo]
	 * - estado igual o mejor que estadoMinimo (si se ha configurado)
	 */
	public boolean cumpleFiltro(Producto2Mano p) {
		if (p == null) return false;
		if (!p.isVisible() || p.isBloqueado()) return false;

		// Sin valoracion no se puede filtrar por precio ni estado
		if (p.getValoracion() == null) return false;

		double valorTasacion = p.getValoracion().getPrecioTasacion();
		if (valorTasacion < valorMinimo || valorTasacion > valorMaximo) return false;

		if (estadoMinimo != null) {
			EstadoProducto estadoProducto = p.getValoracion().getEstadoProducto();
			// NO_ACEPTADO nunca pasa el filtro
			if (estadoProducto == EstadoProducto.NO_ACEPTADO) return false;
			// ordinal mas bajo = mejor estado; rechazamos si el producto es peor que el minimo
			if (estadoProducto.ordinal() > estadoMinimo.ordinal()) return false;
		}

		return true;
	}

	/**
	 * Devuelve el filtro a sus valores por defecto:
	 * valor [0, MAX], sin restriccion de estado.
	 */
	public void resetear() {
		this.valorMinimo  = 0;
		this.valorMaximo  = Double.MAX_VALUE;
		this.estadoMinimo = null;
	}

	// --- Getters y Setters ---

	public double getValorMinimo()         { return valorMinimo; }
	public double getValorMaximo()         { return valorMaximo; }
	public EstadoProducto getEstadoMinimo(){ return estadoMinimo; }

	public void setValorMinimo(double valorMinimo) {
		if (valorMinimo < 0) {
			System.out.println("El valor minimo no puede ser negativo.");
			return;
		}
		if (valorMinimo > this.valorMaximo) {
			System.out.println("El valor minimo no puede ser mayor que el maximo.");
			return;
		}
		this.valorMinimo = valorMinimo;
	}

	public void setValorMaximo(double valorMaximo) {
		if (valorMaximo < this.valorMinimo) {
			System.out.println("El valor maximo no puede ser menor que el minimo.");
			return;
		}
		this.valorMaximo = valorMaximo;
	}

	public void setEstadoMinimo(EstadoProducto estadoMinimo) {
		this.estadoMinimo = estadoMinimo;
	}

	@Override
	public String toString() {
		return "FiltroSegundaMano ["
			+ "valor: " + valorMinimo + "-" + (valorMaximo == Double.MAX_VALUE ? "MAX" : valorMaximo)
			+ " | estado minimo: " + (estadoMinimo == null ? "cualquiera" : estadoMinimo)
			+ "]";
	}
}