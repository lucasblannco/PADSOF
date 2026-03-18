package productos;

import java.time.LocalDateTime;
import java.util.Date;

import usuarios.Empleado;
import ventas.Pago;

public class Valoracion {
	private LocalDateTime fecha;
	private double precioTasacion;
	private EstadoProducto estadoProducto;
	private EstadoValoracion estadoValoracion = EstadoValoracion.PENDIENTE_DE_PAGO;
	private Empleado empleado;
	private Pago pago;

	public Valoracion(double precioTasacion, EstadoProducto estadoProducto, Empleado empleado) {
		this.fecha = LocalDateTime.now();
		this.precioTasacion = precioTasacion;
		this.estadoProducto = estadoProducto;
		this.empleado = empleado;
	}

	public Valoracion(LocalDateTime fecha, double precioTasacion, EstadoProducto estadoProducto,
			EstadoValoracion estadoValoracion, Empleado empleado, Pago pago) {
		this.fecha = fecha;
		this.precioTasacion = precioTasacion;
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

	public double getPrecioTasacion() {
		return precioTasacion;
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
		return "Valoracion [fecha=" + fecha + ", precioTasacion=" + precioTasacion + ", estadoProducto="
				+ estadoProducto + ", estadoValoracion=" + estadoValoracion + ", empleado=" + empleado + ", pago="
				+ pago + ", getFecha()=" + getFecha() + ", getPrecioTasacion()=" + getPrecioTasacion()
				+ ", getEstadoProducto()=" + getEstadoProducto() + ", getEstadoValoracion()=" + getEstadoValoracion()
				+ ", getEmpleado()=" + getEmpleado() + ", getPago()=" + getPago() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	
	
}
