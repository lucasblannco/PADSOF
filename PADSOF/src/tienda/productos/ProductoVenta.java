package productos;

import java.util.*;

import tienda.Estadistica;

public abstract class ProductoVenta extends Producto {
	protected double precioOficial;
	protected int stockDisponible;
	protected ArrayList<Reseña> reseñas;
	protected ArrayList<Categoria> categorias;

	/* CONSTRUCTORES DEL PRODUCTO CON DIFERENTES PARAMETROS */

	public ProductoVenta(String nombre, String descripcion, String imagenRuta, double precioOficial,
			int stockDisponible) {

		super(nombre, descripcion, imagenRuta);
		Estadistica est = Estadistica.getInstancia();
		this.id = "PV" + est.getnProductosVentas();
		est.setnProductosVentas(est.getnProductosVentas() + 1);
		this.precioOficial = precioOficial;
		this.stockDisponible = stockDisponible;
		this.reseñas = new ArrayList<Reseña>();
		this.categorias = new ArrayList<Categoria>();
	}

	public double getMediaPuntuacion() {
		double suma = 0;
		if (this.reseñas.size() == 0) {
			return 0;
		}

		for (Reseña r : this.reseñas) {
			suma += r.getPuntuacion();
		}

		suma = suma / this.reseñas.size();

		return suma;
	}

	public ArrayList<Reseña> getReseñas() {
		return this.reseñas;
	}

	public int getStockDisponible() {
		return this.stockDisponible;
	}

	public void setStockDisponible(int cantidad) {
		if (cantidad >= 0) {
			this.stockDisponible = cantidad;
		}
	}

	public ArrayList<Categoria> getCategorias() {
		return this.categorias;
	}

	public boolean addCategoria(Categoria c) {
		if (c == null) {
			return false;
		}
		if (this.categorias.contains(c)) {
			return false;
		}

		this.categorias.add(c);
		c.addProductoInterno(this);
		return true;
	}

	public boolean deleteCategoria(Categoria c) {
		if (c == null) {
			return false;
		}
		if (!this.categorias.contains(c)) {
			return false;
		}

		this.categorias.remove(c);
		c.deleteProductoInterno(this);
		return true;
	}

	protected boolean addCategoriaInterno(Categoria c) {
		if (c == null || this.categorias.contains(c)) {
			return false;
		}
		this.categorias.add(c);
		return true;
	}

	protected boolean deleteCategoriaInterno(Categoria c) {
		if (c == null || !this.categorias.contains(c)) {
			return false;
		}
		this.categorias.remove(c);
		return true;
	}

	public boolean addReseña(Reseña r) {
		if (r == null)
			return false;
		if (this.reseñas.contains(r))
			return false;

		for (Reseña existente : this.reseñas) {
			if (existente.getAutor() != null && existente.getAutor().equals(r.getAutor())) {
				System.out.println("Este cliente ya ha reseñado este producto.");
				return false;
			}
		}

		this.reseñas.add(r);
		r.setProducto(this);
		return true;
	}

	public boolean deleteReseña(Reseña r) {
		if (r == null) {
			return false;
		}
		if (!this.reseñas.contains(r)) {
			return false;
		}

		this.reseñas.remove(r);
		return true;
	}

	public double getPrecioVenta() {
		return this.precioOficial;
	}
	

	@Override
	public String toString() {
		return "ProductoVenta [precioOficial=" + precioOficial + ", stockDisponible=" + stockDisponible + ", reseñas="
				+ reseñas + ", categorias=" + categorias + ", toString()=" + super.toString() + "]\n";
	}

	public double getPrecioOficial() {
		return precioOficial;
	}

	public void setPrecioOficial(double precioOficial) {
		this.precioOficial = precioOficial;
	}

}
