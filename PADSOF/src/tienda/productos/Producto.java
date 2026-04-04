package productos;

import Excepcion.ProductoInvalidoException;

public abstract class Producto {
	protected String id;
	protected String nombre;
	protected String descripcion;
	protected String imagenRuta;

	public Producto(String nombre, String descripcion, String imagenRuta) {
		if (nombre == null || nombre.isBlank()) {
			throw new ProductoInvalidoException("El nombre del producto no puede estar vacío.");
		}
		if (descripcion == null || descripcion.isBlank()) {
			throw new ProductoInvalidoException("La descripción del producto no puede estar vacía.");
		}
		if (imagenRuta == null || imagenRuta.isBlank()) {
			throw new ProductoInvalidoException("La ruta de la imagen no puede estar vacía.");
		}

		this.id = "0";
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.imagenRuta = imagenRuta;
	}

	public String getId() {
		return this.id;
	}

	public String getNombre() {
		return this.nombre;
	}

	@Override
	public String toString() {
		return "[" + this.id + "] " + this.getNombre() + " | Imagen: " + this.getImagenRuta() + " |";
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		if (descripcion == null || descripcion.isBlank()) {
			throw new ProductoInvalidoException("La descripción del producto no puede estar vacía.");
		}
		this.descripcion = descripcion;
	}

	public String getImagenRuta() {
		return imagenRuta;
	}

	public void setImagenRuta(String imagenRuta) {
		if (imagenRuta == null || imagenRuta.isBlank()) {
			throw new ProductoInvalidoException("La ruta de la imagen no puede estar vacía.");
		}
		this.imagenRuta = imagenRuta;
	}
}
