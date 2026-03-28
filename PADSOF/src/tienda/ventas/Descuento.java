package ventas;

import java.time.LocalDateTime;

import tienda.Estadistica;

public abstract class Descuento {
	protected String id;
	protected String nombre;
	protected LocalDateTime fechaInicio;
	protected LocalDateTime fechaFin;

	public Descuento(String nombre, LocalDateTime inicio, LocalDateTime fin) {
		Estadistica est = Estadistica.getInstancia();
		this.id = "DESC-" + String.valueOf(est.getnDescuentos());
		est.setnDescuentos(est.getnDescuentos() + 1);
		this.nombre = nombre;
		this.fechaInicio = inicio;
		this.fechaFin = fin;
	}

	public boolean estaActivo() {
		LocalDateTime ahora = LocalDateTime.now();
		return ahora.isAfter(fechaInicio) && ahora.isBefore(fechaFin);
	}

	public abstract double aplicarDescuento(Carrito carrito);

	// --- GETTERS Y SETTERS ---

	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDateTime fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDateTime getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDateTime fechaFin) {
		this.fechaFin = fechaFin;
	}
	public String getId() {
		return this.id;
	}
}
