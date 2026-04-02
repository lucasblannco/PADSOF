package tienda;

import java.util.ArrayList;
import java.util.List;
import productos.Categoria;
import productos.ProductoVenta;

public class FiltroVenta {

	private double precioMinimo;
	private double precioMaximo;
	private double puntuacionMinima;
	private List<Categoria> categorias;

	public FiltroVenta() {
		resetear();
	}

	/**
	 * Devuelve true si el producto cumple todos los criterios activos del filtro:
	 * - precio dentro del rango [precioMinimo, precioMaximo]
	 * - media de puntuacion >= puntuacionMinima
	 * - pertenece a al menos una de las categorias del filtro (si hay alguna)
	 */
	public boolean productoCumpleFiltro(ProductoVenta p) {
		if (p == null) return false;

		if (p.getPrecioOficial() < precioMinimo || p.getPrecioOficial() > precioMaximo) return false;

		if (p.getMediaPuntuacion() < puntuacionMinima) return false;

		if (!categorias.isEmpty()) {
			boolean tieneCategoria = false;
			for (Categoria cat : categorias) {
				if (p.getCategorias().contains(cat)) {
					tieneCategoria = true;
					break;
				}
			}
			if (!tieneCategoria) return false;
		}

		return true;
	}

	/**
	 * Devuelve el filtro a sus valores por defecto:
	 * precio [0, MAX], puntuacion 0, sin categorias.
	 */
	public void resetear() {
		this.precioMinimo     = 0;
		this.precioMaximo     = Double.MAX_VALUE;
		this.puntuacionMinima = 0;
		this.categorias       = new ArrayList<>();
	}

	// --- Getters y Setters ---

	public double getPrecioMinimo()     { return precioMinimo; }
	public double getPrecioMaximo()     { return precioMaximo; }
	public double getPuntuacionMinima() { return puntuacionMinima; }
	public List<Categoria> getCategorias() { return categorias; }

	public void setPrecioMinimo(double precioMinimo) {
		if (precioMinimo < 0) {
			System.out.println("El precio minimo no puede ser negativo.");
			return;
		}
		if (precioMinimo > this.precioMaximo) {
			System.out.println("El precio minimo no puede ser mayor que el maximo.");
			return;
		}
		this.precioMinimo = precioMinimo;
	}

	public void setPrecioMaximo(double precioMaximo) {
		if (precioMaximo < this.precioMinimo) {
			System.out.println("El precio maximo no puede ser menor que el minimo.");
			return;
		}
		this.precioMaximo = precioMaximo;
	}

	public void setPuntuacionMinima(double puntuacionMinima) {
		if (puntuacionMinima < 0 || puntuacionMinima > 10) {
			System.out.println("La puntuacion minima debe estar entre 0 y 10.");
			return;
		}
		this.puntuacionMinima = puntuacionMinima;
	}

	public void añadirCategoria(Categoria c) {
		if (c != null && !categorias.contains(c))
			categorias.add(c);
	}

	public void eliminarCategoria(Categoria c) {
		categorias.remove(c);
	}

	@Override
	public String toString() {
		String cats = categorias.isEmpty() ? "todas" :
			categorias.stream().map(Categoria::getNombre)
				.reduce((a, b) -> a + ", " + b).orElse("");
		return "FiltroVenta ["
			+ "precio: " + precioMinimo + "-" + (precioMaximo == Double.MAX_VALUE ? "MAX" : precioMaximo)
			+ " | puntuacion min: " + puntuacionMinima
			+ " | categorias: " + cats + "]";
	}
}