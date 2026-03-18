package productos;

import java.util.*;

public abstract class Producto {
	protected final String id;
	protected String nombre;
	protected String descripcion;
	protected String imagenRuta;

	public Producto(String nombre, String descripcion, String imagenRuta) {
		this.id = UUID.randomUUID().toString().substring(0, 8);
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
		return "Producto [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + ", imagenRuta="
				+ imagenRuta + ", getId()=" + getId() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

}
