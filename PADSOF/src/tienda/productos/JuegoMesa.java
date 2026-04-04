package productos;

import Excepcion.ProductoInvalidoException;

public class JuegoMesa extends ProductoVenta {
	private int minJugadores;
	private int maxJugadores;
	private int minEdad;
	private int maxEdad;
	private String tipoJuego;

	public JuegoMesa(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			int minJugadores, int maxJugadores, int minEdad, int maxEdad, String tipoJuego) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible);

		if (minJugadores <= 0) {
			throw new ProductoInvalidoException("El número mínimo de jugadores debe ser mayor que 0.");
		}
		if (maxJugadores < minJugadores) {
			throw new ProductoInvalidoException("El número máximo de jugadores no puede ser menor que el mínimo.");
		}
		if (minEdad < 0) {
			throw new ProductoInvalidoException("La edad mínima no puede ser negativa.");
		}
		if (maxEdad < minEdad) {
			throw new ProductoInvalidoException("La edad máxima no puede ser menor que la edad mínima.");
		}
		if (tipoJuego == null || tipoJuego.isBlank()) {
			throw new ProductoInvalidoException("El tipo de juego no puede estar vacío.");
		}

		this.minJugadores = minJugadores;
		this.maxJugadores = maxJugadores;
		this.minEdad = minEdad;
		this.maxEdad = maxEdad;
		this.tipoJuego = tipoJuego;
	}

	@Override
	public String toString() {
		return super.toString() + " | MinJugadores: " + this.minJugadores + " | MaxJugadores: " + this.maxJugadores
				+ " | EdadMin: " + this.minEdad + " | EdadMax: " + this.maxEdad + " | Tipo: " + this.tipoJuego + " |";
	}
}
