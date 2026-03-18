package productos;

public class Figura extends ProductoVenta {
	private double altura;
	private double ancho;
	private double largo;
	private String material;
	private String marca;

	public Figura(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			boolean enOferta, boolean promocionable, double altura, double ancho, double largo, String material,
			String marca) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible, enOferta, promocionable);
		this.altura = altura;
		this.ancho = ancho;
		this.largo = largo;
		this.material = material;
		this.marca = marca;
	}

	@Override
	public String toString() {
		return "Figura [altura=" + altura + ", ancho=" + ancho + ", largo=" + largo + ", material=" + material
				+ ", marca=" + marca + ", precioOficial=" + precioOficial + ", stockDisponible=" + stockDisponible
				+ ", enOferta=" + enOferta + ", reseñas=" + reseñas + ", categorias=" + categorias + ", promocionable="
				+ promocionable + ", id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + ", imagenRuta="
				+ imagenRuta + ", getMediaPuntuacion()=" + getMediaPuntuacion() + ", getPrecioOficial()="
				+ getPrecioOficial() + ", getReseñas()=" + getReseñas() + ", getStockDisponible()="
				+ getStockDisponible() + ", getCategorias()=" + getCategorias() + ", getId()=" + getId()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}

}
