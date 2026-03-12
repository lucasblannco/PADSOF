package productos;

import java.time.LocalDateTime;
import java.util.Date;

import usuarios.Empleado;
import ventas.Pago;

public class Valoracion {
	private LocalDateTime fecha = null;
	private double precioTasacion;
	private EstadoProducto estadoProducto = null;
	private EstadoValoracion estadoValoracion = EstadoValoracion.PENDIENTE_DE_PAGO;
	private Empleado empleado = null;
	private Pago pago = null;

	public Valoracion(double precioTasacion, EstadoProducto estado, Empleado empleado) {
		this.fecha = LocalDateTime.now();
		this.precioTasacion = precioTasacion;
		this.estadoProducto = estado;
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

	/* COMO HACEMOS PARA MIRAR QUE UNA TARJETA ES VALIDA (algoritmo de luhn) */
	public boolean pagar(String tarjeta, int cvv, Date caducidad) {

		Pago pagoTarjeta = new Pago(tarjeta, this.precioTasacion, caducidad, cvv);
		if (pago.getExito() == false) {
			return false;
		}

		this.pago = pagoTarjeta;

		this.estadoValoracion = EstadoValoracion.PAGADO;

		this.fecha = LocalDateTime.now();
		return true;

	}


	public void setEstadoValoracion(EstadoValoracion estadoValoracion) {
		this.estadoValoracion = estadoValoracion;
	}
	
}




