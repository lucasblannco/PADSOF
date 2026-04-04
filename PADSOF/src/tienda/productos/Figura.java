package productos;

import Excepcion.ProductoInvalidoException;

public class Figura extends ProductoVenta {
	private double altura;
	private double ancho;
	private double largo;
	private String material;
	private String marca;

	public Figura(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			double altura, double ancho, double largo, String material, String marca) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible);

		if (altura <= 0) {
			throw new ProductoInvalidoException("La altura debe ser mayor que 0.");
		}
		if (ancho <= 0) {
			throw new ProductoInvalidoException("El ancho debe ser mayor que 0.");
		}
		if (largo <= 0) {
			throw new ProductoInvalidoException("El largo debe ser mayor que 0.");
		}
		if (material == null || material.isBlank()) {
			throw new ProductoInvalidoException("El material no puede estar vacío.");
		}
		if (marca == null || marca.isBlank()) {
			throw new ProductoInvalidoException("La marca no puede estar vacía.");
		}

		this.altura = altura;
		this.ancho = ancho;
		this.largo = largo;
		this.material = material;
		this.marca = marca;
	}

	@Override
	public String toString() {
		return super.toString() + " | Altura: " + this.altura + " | Ancho: " + this.ancho + " | Largo: " + this.largo
				+ " | Material: " + this.material + " | Marca: " + this.marca + " |";
	}
}
