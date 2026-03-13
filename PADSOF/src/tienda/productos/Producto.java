package productos;

import java.util.*;

public abstract class Producto {
	protected String id;
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

}
