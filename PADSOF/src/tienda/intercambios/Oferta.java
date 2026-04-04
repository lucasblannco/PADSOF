package intercambios;

import java.time.LocalDateTime;
import java.util.List;

import Excepcion.*; // Importamos tus nuevas excepciones
import tienda.Estadistica;
import tienda.Tienda;
import tienda.TipoNotificacion;
import productos.*;
import usuarios.*;

public class Oferta {
	private String id;
	private LocalDateTime fechaOferta;
	private EstadoOferta estado;
	private Cliente origen;
	private Cliente destino;
	private List<Producto2Mano> productosOfertados;
	private List<Producto2Mano> productosSolicitados;

	public Oferta(Cliente origen, Cliente destino, List<Producto2Mano> productosOfertados,
			List<Producto2Mano> productosSolicitados) throws ProductoNoTasadoException {

		// Validamos que todos los productos ofertados estén tasados
		for (Producto2Mano p : productosOfertados) {
			if (p.getEstado() == null) { // Si el estado es null, es que no ha sido tasado
				throw new ProductoNoTasadoException(p.getId(), p.getNombre());
			}
		}
		// Validamos que todos los productos solicitados estén tasados
		for (Producto2Mano p : productosSolicitados) {
			if (p.getEstado() == null) {
				throw new ProductoNoTasadoException(p.getId(), p.getNombre());
			}
		}

		Estadistica est = Estadistica.getInstancia();
		this.id = "OFER-" + String.valueOf(est.getnIntercambiosFinalizados());
		est.setnIntercambiosFinalizados(est.getnIntercambiosFinalizados() + 1);
		this.fechaOferta = LocalDateTime.now();
		this.estado = EstadoOferta.PENDIENTE;
		this.origen = origen;
		this.destino = destino;
		this.productosOfertados = productosOfertados;
		this.productosSolicitados = productosSolicitados;
	}

	public void rechazar() throws OfertaNoDisponibleException {
		// Validamos disponibilidad
		if (this.estado != EstadoOferta.PENDIENTE || haCaducado()) {
			throw new OfertaNoDisponibleException(this.id);
		}

		this.estado = EstadoOferta.RECHAZADA;
		// Importante: desbloqueamos los productos para que vuelvan a estar disponibles
		for (Producto2Mano p : productosOfertados)
			p.setBloqueado(false);// Los productos ofertados por el cliente que ha
		// propuesto la oferta son desbloqueados y despues los vamos a poder usar para
		// mas ofertas
		this.origen.getOfertasPendientes().remove(this);
		this.destino.getOfertasPendientes().remove(this);
		this.origen.recibirNotificacionTipo("Tu oferta con ID " + this.getId() + " ha sido RECHAZADA por el cliente "
				+ this.destino.getNickname() + ".", TipoNotificacion.OFERTA_RECHAZADA);
	}

	public void aceptarOferta() throws OfertaNoDisponibleException {
		// Validamos disponibilidad
		if (this.estado != EstadoOferta.PENDIENTE || haCaducado()) {
			throw new OfertaNoDisponibleException(this.id);
		}
		this.estado = EstadoOferta.ACEPTADA;
	}

	public void aceptarYEjecutar() throws OfertaNoDisponibleException {
		// Validamos disponibilidad antes de ejecutar
		if (this.estado != EstadoOferta.PENDIENTE && this.estado != EstadoOferta.ACEPTADA) {
			throw new OfertaNoDisponibleException(this.id);
		}
		if (haCaducado()) {
			throw new OfertaNoDisponibleException(this.id);
		}

		origen.getHistorialIntercambios().add(this);
		destino.getHistorialIntercambios().add(this);
		origen.getOfertasPendientes().remove(this);
		destino.getOfertasPendientes().remove(this);

		for (Producto2Mano p : this.productosOfertados) {
			origen.getCarteraIntercambio().remove(p);
			p.setBloqueado(false);
			// AHORA SE ENVIARIAN
		}
		for (Producto2Mano p : productosSolicitados) {
			destino.getCarteraIntercambio().remove(p);
			// AHORA SE ENVIARIAN
		}
		Tienda.getInstancia().registrarIntercambioFinalizado(this);
		origen.recibirNotificacionTipo("¡Intercambio ID " + this.id + " aceptado por el usuario "
				+ this.getDestino().getNickname() + "! Preparando envío.", TipoNotificacion.INTERCAMBIO_REALIZADO);
		destino.recibirNotificacionTipo("Has aceptado el intercambio con el usuario " + this.origen.getNickname()
				+ ". Los productos han salido de tu inventario.", TipoNotificacion.INTERCAMBIO_REALIZADO);
		this.estado = EstadoOferta.REALIZADA;
	}

	public boolean haCaducado() {
		int tiempoMaxOferta = Tienda.getInstancia().getTiempoMaxOferta();
		return LocalDateTime.now().isAfter(fechaOferta.plusMinutes(tiempoMaxOferta));// Comprobamos si el tiempo en el
																						// que finaliza la oferta es
																						// anterior al tiempo real de
																						// ahora
	}

	public void imprimirResumen() {
		System.out.println("Resumen de la oferta:");
		System.out.println("  [" + id + "]" + " | estado: " + estado + " | " + origen.getNickname() + " -> "
				+ destino.getNickname());
		System.out.println("  Productos ofertados por " + origen.getNickname() + ":");
		for (Producto2Mano p : productosOfertados) {
			System.out.println("   -> " + p.resumen());
		}
		System.out.println("  Productos solicitados a " + destino.getNickname() + ":");
		for (Producto2Mano p : productosSolicitados) {
			System.out.println("   -> " + p.resumen());
		}
	}

	// Getters y Setters
	public String getId() {
		return id;
	}

	public LocalDateTime getFechaOferta() {
		return fechaOferta;
	}

	public EstadoOferta getEstado() {
		return estado;
	}

	public void setEstado(EstadoOferta estado) {
		this.estado = estado;
	}

	public List<Producto2Mano> getProductosOfertados() {
		return productosOfertados;
	}

	public List<Producto2Mano> getProductosSolicitados() {
		return productosSolicitados;
	}

	public Cliente getOrigen() {
		return this.origen;
	}

	public Cliente getDestino() {
		return this.destino;
	}
}