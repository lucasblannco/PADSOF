package productos;

import java.time.LocalDateTime;
import java.util.Date;

import usuarios.Empleado;
import ventas.Pago;

public class Valoracion {
	private LocalDateTime fecha;
	private EstadoProducto estadoProducto;
	private EstadoValoracion estadoValoracion = EstadoValoracion.PENDIENTE_DE_PAGO;
	private Empleado empleado;
	private Pago pago;
	private double precioTasacion;

	public Valoracion(double precioTasacion, EstadoProducto estadoProducto, Empleado empleado) {
		this.fecha = LocalDateTime.now();
		this.estadoProducto = estadoProducto;
		this.empleado = empleado;
		this.precioTasacion=precioTasacion;
	}

	public Valoracion(LocalDateTime fecha, double precioTasacion, EstadoProducto estadoProducto,
			EstadoValoracion estadoValoracion, Empleado empleado, Pago pago) {
		this.fecha = fecha;

		this.estadoProducto = estadoProducto;
		this.estadoValoracion = estadoValoracion;
		this.empleado = empleado;
		this.pago = pago;
	}

	public boolean pagar(String tarjeta, int cvv, Date caducidad) {
		Pago pagoTarjeta = new Pago(tarjeta, this.precioTasacion, caducidad, cvv);

		if (!pagoTarjeta.getExito()) {
			return false;
		}

		this.pago = pagoTarjeta;
		this.estadoValoracion = EstadoValoracion.PAGADO;
		return true;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public EstadoProducto getEstadoProducto() {
		return estadoProducto;
	}

	public EstadoValoracion getEstadoValoracion() {
		return estadoValoracion;
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public Pago getPago() {
		return pago;
	}

	public void setEstadoValoracion(EstadoValoracion estadoValoracion) {
		this.estadoValoracion = estadoValoracion;
	}

	@Override
	public String toString() {
		return "[" + this.estadoValoracion + "] " + (this.empleado != null ? this.empleado.getNickname() : "null")
				+ " | " + this.fecha + " | " + this.estadoProducto + " |";
	}

	public double getPrecioTasacion() {
		return precioTasacion;
	}

	public void setPrecioTasacion(double precioTasacion) {
		this.precioTasacion = precioTasacion;
	}

}
