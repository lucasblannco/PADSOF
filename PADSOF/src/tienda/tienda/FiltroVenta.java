package tienda;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import productos.Categoria;
import productos.ProductoVenta;

public class FiltroVenta {
	private double precioMinimo;
	private double precioMaximo;
	private double puntuacionMinima;
	private List<Categoria> categorias;

	public FiltroVenta() {
		this.precioMinimo = 0;
		this.precioMaximo = Double.MAX_VALUE;
		this.puntuacionMinima = 0;
		this.categorias = new ArrayList<>();
	}

	public boolean productoCumpleFiltro(ProductoVenta p) {
		if (p == null) {
			return false;
		}
		if (p.getPrecioOficial() < precioMinimo || p.getPrecioOficial() > precioMaximo) {
			return false;
		}
		if (p.getMediaPuntuacion() < puntuacionMinima) {
			return false;
		}

		// comprobemos que haya laguna categoria en el filtro
		if (!categorias.isEmpty()) {
			boolean tieneCategoria = false;
			for (Categoria cat : categorias) {
				if (p.getCategorias().contains(cat)) {
					tieneCategoria = true;
					break;
				}
			}
			if (!tieneCategoria) {
				return false;
			}
		}
		return true;
	}

// Getters y setters
	public double getPrecioMinimo() {
		return precioMinimo;
	}

	public double getPrecioMaximo() {
		return precioMaximo;
	}

	public double getPuntuacionMinima() {
		return puntuacionMinima;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setPrecioMinimo(double precioMinimo) {
		this.precioMinimo = precioMinimo;
	}

	public void setPrecioMaximo(double precioMaximo) {
		this.precioMaximo = precioMaximo;
	}

	public void setPuntuacionMinima(double puntuacionMinima) {
		this.puntuacionMinima = puntuacionMinima;
	}

	public void añadirCategoria(Categoria c) {
		if (c != null && !categorias.contains(c))
			categorias.add(c);
	}

	public void eliminarCategoria(Categoria c) {
		categorias.remove(c);
	}

}
