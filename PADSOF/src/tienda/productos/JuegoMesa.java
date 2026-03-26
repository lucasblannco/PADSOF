package productos;

public class JuegoMesa extends ProductoVenta {
	private int minJugadores;
	private int maxJugadores;
	private int minEdad;
	private int maxEdad;
	private String tipoJuego;

	public JuegoMesa(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			int minJugadores, int maxJugadores, int minEdad, int maxEdad, String tipoJuego) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible);
		this.minJugadores = minJugadores;
		this.maxJugadores = maxJugadores;
		this.minEdad = minEdad;
		this.maxEdad = maxEdad;
		this.tipoJuego = tipoJuego;
	}

	@Override
	public String toString() {
		return "JuegoMesa [minJugadores=" + minJugadores + ", maxJugadores=" + maxJugadores + ", minEdad=" + minEdad
				+ ", maxEdad=" + maxEdad + ", tipoJuego=" + tipoJuego + ", toString()=" + super.toString() + "]";
	}

}
