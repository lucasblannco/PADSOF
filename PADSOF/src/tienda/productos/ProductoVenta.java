package productos;

import java.util.*;

import usuarios.Cliente;

public abstract class ProductoVenta extends Producto {
	protected double precioOficial;
	protected int stockDisponible;
	protected boolean enOferta;
	protected ArrayList<Reseña> reseñas;

	/* Preguntar por este atributo */
	protected boolean promocionable;

	/* CONSTRUCTORES DEL PRODUCTO CON DIFERENTES PARAMETROS */

	public ProductoVenta(String nombre, String descripcion, String imagenRuta, double precioOficial,
			int stockDisponible, boolean enOferta, boolean promocionable) {

		super(nombre, descripcion, imagenRuta);
		this.precioOficial = precioOficial;
		this.stockDisponible = stockDisponible;
		this.enOferta = enOferta;
		this.promocionable = promocionable;
		this.reseñas = new ArrayList<Reseña>();
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

	public boolean añadirReseña(Reseña r) {

		if (r == null) {
			return false;
		}

		this.reseñas.add(r);
		return true;
	}

	public double getPrecioOficial() {
		return this.precioOficial;
	}
	
	public ArrayList<Reseña> getReseñas(){
		return this.reseñas;
	}
	
	public int getStockDisponible() {
	    return this.stockDisponible;
	}

}
