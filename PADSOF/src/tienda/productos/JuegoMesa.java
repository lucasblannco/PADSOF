package productos;

public class JuegoMesa extends ProductoVenta {
	private int minJugadores;
	private int maxJugadores;
	private int minEdad;
	private int maxEdad;
	private String tipoJuego;

	public JuegoMesa(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			boolean enOferta, boolean promocionable, int minJugadores, int maxJugadores, int minEdad, int maxEdad,
			String tipoJuego) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible, enOferta, promocionable);
		this.minJugadores = minJugadores;
		this.maxJugadores = maxJugadores;
		this.minEdad = minEdad;
		this.maxEdad = maxEdad;
		this.tipoJuego = tipoJuego;
	}

	@Override
	public String toString() {
		return "JuegoMesa [minJugadores=" + minJugadores + ", maxJugadores=" + maxJugadores + ", minEdad=" + minEdad
				+ ", maxEdad=" + maxEdad + ", tipoJuego=" + tipoJuego + ", precioOficial=" + precioOficial
				+ ", stockDisponible=" + stockDisponible + ", enOferta=" + enOferta + ", reseñas=" + reseñas
				+ ", categorias=" + categorias + ", promocionable=" + promocionable + ", id=" + id + ", nombre="
				+ nombre + ", descripcion=" + descripcion + ", imagenRuta=" + imagenRuta + ", getMediaPuntuacion()="
				+ getMediaPuntuacion() + ", getPrecioOficial()=" + getPrecioVenta() + ", getReseñas()=" + getReseñas()
				+ ", getStockDisponible()=" + getStockDisponible() + ", getCategorias()=" + getCategorias()
				+ ", getId()=" + getId() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

}
