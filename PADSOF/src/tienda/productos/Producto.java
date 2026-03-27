package productos;

import java.util.*;

public abstract class Producto {
	protected String id;
	protected String nombre;
	protected String descripcion;
	protected String imagenRuta;

	public Producto(String nombre, String descripcion, String imagenRuta) {
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
		return "Producto [id=" + id + ", nombre=" + nombre + ", imagenRuta=" + imagenRuta + "]";
	}

}
