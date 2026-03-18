package productos;

public class Comic extends ProductoVenta {
	private int numeroPaginas;
	private String editorial;
	private int añoPublicacion;
	
	public Comic(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			boolean enOferta, boolean promocionable, int numeroPaginas, String editorial, int añoPublicacion) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible, enOferta, promocionable);
		this.numeroPaginas = numeroPaginas;
		this.editorial = editorial;
		this.añoPublicacion = añoPublicacion;
	}

	@Override
	public String toString() {
		return "Comic [numeroPaginas=" + numeroPaginas + ", editorial=" + editorial + ", añoPublicacion="
				+ añoPublicacion + ", precioOficial=" + precioOficial + ", stockDisponible=" + stockDisponible
				+ ", enOferta=" + enOferta + ", reseñas=" + reseñas + ", categorias=" + categorias + ", promocionable="
				+ promocionable + ", id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + ", imagenRuta="
				+ imagenRuta + ", getMediaPuntuacion()=" + getMediaPuntuacion() + ", getPrecioOficial()="
				+ getPrecioOficial() + ", getReseñas()=" + getReseñas() + ", getStockDisponible()="
				+ getStockDisponible() + ", getCategorias()=" + getCategorias() + ", getId()=" + getId()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}



}
