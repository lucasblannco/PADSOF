package productos;

public class Figura extends ProductoVenta {
	private double altura;
	private double ancho;
	private double largo;
	private String material;
	private String marca;

	public Figura(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			double altura, double ancho, double largo, String material, String marca) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible);
		this.altura = altura;
		this.ancho = ancho;
		this.largo = largo;
		this.material = material;
		this.marca = marca;
	}

	@Override
	public String toString() {
		return "Figura [altura=" + altura + ", ancho=" + ancho + ", largo=" + largo + ", material=" + material
				+ ", marca=" + marca + ", toString()=" + super.toString() + "]";
	}
	
}
