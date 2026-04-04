package productos;

import Excepcion.ProductoInvalidoException;

public class Comic extends ProductoVenta {
	private int numeroPaginas;
	private String editorial;
	private int añoPublicacion;

	public Comic(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			int numeroPaginas, String editorial, int añoPublicacion) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible);

		if (numeroPaginas <= 0) {
			throw new ProductoInvalidoException("El número de páginas debe ser mayor que 0.");
		}
		if (editorial == null || editorial.isBlank()) {
			throw new ProductoInvalidoException("La editorial no puede estar vacía.");
		}
		if (añoPublicacion <= 0) {
			throw new ProductoInvalidoException("El año de publicación no es válido.");
		}

		this.numeroPaginas = numeroPaginas;
		this.editorial = editorial;
		this.añoPublicacion = añoPublicacion;
	}

	@Override
	public String toString() {
		return super.toString() + " | Paginas : " + this.numeroPaginas + " | Editorial: " + this.editorial
				+ " AñoPublicacion: " + this.añoPublicacion + " |";
	}
}
