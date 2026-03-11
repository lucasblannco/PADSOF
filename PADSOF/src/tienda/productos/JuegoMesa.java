package productos;

import java.util.*;

public class JuegoMesa extends ProductoVenta {
	private int minJugadores;
	private int maxJugadores;
	private int minEdad;
	private int maxEdad;
	private String tipoJuego;

	public JuegoMesa(String id, String nombre, String descripcion, String imagenRuta, double precioOficial,
			int stockDisponible, boolean enOferta, ArrayList<Reseña> reseñas, boolean promocionable, int minJugadores,
			int maxJugadores, int minEdad, int maxEdad, String tipoJuego) {
		super(id, nombre, descripcion, imagenRuta, precioOficial, stockDisponible, enOferta, reseñas, promocionable);
		this.minJugadores = minJugadores;
		this.maxJugadores = maxJugadores;
		this.minEdad = minEdad;
		this.maxEdad = maxEdad;
		this.tipoJuego = tipoJuego;
	}

}
