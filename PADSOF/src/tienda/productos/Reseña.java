package productos;

import usuarios.Cliente;
import java.util.*;
import java.time.*;

public class Reseña {
	private String idReseña;
	private Cliente autor;
	private ProductoVenta producto;
	private double puntuacion; // 0-10
	private String comentario;
	private LocalDate fecha;

	public Reseña(Cliente autor, ProductoVenta producto, double puntuacion, String comentario) {
		this.idReseña = UUID.randomUUID().toString().substring(0, 8);
		this.autor = autor;
		this.producto = null;

		if (puntuacion < 0) {
			puntuacion = 0;
		}
		if (puntuacion > 10) {
			puntuacion = 10;
		}
		this.puntuacion = puntuacion;

		this.comentario = (comentario == null) ? "" : comentario;
		this.fecha = LocalDate.now();

		if (producto != null) {
			producto.addReseña(this);
		}
	}

	public LocalDate getFecha() {
		return this.fecha;
	}

	public double getPuntuacion() {
		return this.puntuacion;
	}

	public boolean setProducto(ProductoVenta p) {
		if (p == null) {
			return false;
		}
		this.producto = p;
		return true;
	}

	@Override
	public String toString() {
		return "Reseña [idReseña=" + idReseña + ", autor=" + autor + ", producto=" + producto + ", puntuacion="
				+ puntuacion + ", comentario=" + comentario + ", fecha=" + fecha + ", toString()=" + super.toString()
				+ "]";
	}

}
