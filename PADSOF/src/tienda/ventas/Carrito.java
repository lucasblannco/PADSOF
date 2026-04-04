package ventas;

import java.time.*;
import java.util.*;
import tienda.Estadistica;
import tienda.Tienda;
import tienda.TipoNotificacion;
import usuarios.Cliente;
import productos.ProductoVenta;

public class Carrito {
	private final String idCarrito;
	private final List<LineaCarrito> lineas;
	private final LocalDateTime fechaCreacion;
	private Descuento descuentoAplicado;
	private final Cliente propietario;

	public Carrito(Cliente propietario) {
		Estadistica est = Estadistica.getInstancia();
		this.idCarrito = "CARRITO-" + String.valueOf(est.getnCarritos());
		est.setnCarritos(est.getnCarritos() + 1);
		this.lineas = new ArrayList<>();
		this.fechaCreacion = LocalDateTime.now();
		this.descuentoAplicado = null;
		this.propietario = propietario;
	}

	public boolean añadirProducto(ProductoVenta p, int cantidad) {
		if (this.estaCaducado() == true) {
			caducar();
			return false;
		}

		if (p == null || cantidad < 1 || p.getStockDisponible() < cantidad) {
			return false;
		}

		for (LineaCarrito l : this.lineas) {
			if (l.productoPertence(p)) {
				l.setCantidad(l.getCantidad() + cantidad);
				p.setStockDisponible(p.getStockDisponible() - cantidad);
				Tienda.getInstancia().aplicarDescuentoPrioritario(this);
				return true;
			}
		}

		LineaCarrito nuevaLinea = new LineaCarrito(p, cantidad);
		this.lineas.add(nuevaLinea);
		p.setStockDisponible(p.getStockDisponible() - cantidad);
		Tienda.getInstancia().aplicarDescuentoPrioritario(this);
		return true;
	}

	public double getTotal() {
		if (this.descuentoAplicado == null) {
			return calcularSubtotal();
		}

		if (this.descuentoAplicado instanceof Regalo) {
			return calcularSubtotal();
		}

		return this.descuentoAplicado.aplicarDescuento(this);
	}

	public boolean eliminarProducto(ProductoVenta p) {
		if (this.estaCaducado() == true) {
			caducar();
			return false;
		}

		if (p == null) {
			return false;
		}

		LineaCarrito lineaAEliminar = null;

		for (LineaCarrito l : this.lineas) {
			if (l.productoPertence(p)) {
				p.setStockDisponible(p.getStockDisponible() + l.getCantidad());
				lineaAEliminar = l;
				break;
			}
		}

		if (lineaAEliminar != null) {
			this.lineas.remove(lineaAEliminar);
			Tienda.getInstancia().aplicarDescuentoPrioritario(this);
			return true;
		}

		return false;
	}

	public boolean cambiarCantidadProducto(ProductoVenta p, int nuevaCantidad) {
		if (this.estaCaducado() == true) {
			caducar();
			return false;
		}

		if (p == null || nuevaCantidad < 0) {
			return false;
		}

		if (nuevaCantidad == 0) {
			return this.eliminarProducto(p);
		}

		for (LineaCarrito l : this.lineas) {
			if (l.productoPertence(p)) {
				int cantidadActual = l.getCantidad();
				int diferencia = nuevaCantidad - cantidadActual;

				if (diferencia > 0 && p.getStockDisponible() < diferencia) {
					return false;
				}

				p.setStockDisponible(p.getStockDisponible() - diferencia);
				l.setCantidad(nuevaCantidad);
				Tienda.getInstancia().aplicarDescuentoPrioritario(this);
				return true;
			}
		}

		return false;
	}

	public void vaciarCarrito() {
		for (LineaCarrito l : this.lineas) {
			ProductoVenta p = l.getProducto();
			p.setStockDisponible(p.getStockDisponible() + l.getCantidad());
		}
		this.lineas.clear();
		this.descuentoAplicado = null;
	}

	public double calcularSubtotal() {
		double suma = 0;

		for (LineaCarrito l : this.lineas) {
			suma += l.getSubtotal();
		}

		return suma;
	}

	public boolean estaCaducado() {
		int tiempoMax = Tienda.getInstancia().getTiempoMaxCarrito();
		if (tiempoMax == 0)
			return false;
		return LocalDateTime.now().isAfter(this.fechaCreacion.plusMinutes(tiempoMax));
	}

	public void caducar() {
		vaciarCarrito();
		if (this.propietario != null) {
			this.propietario.recibirNotificacionTipo("Tu carrito ha caducado y los productos han sido liberados.",
					TipoNotificacion.CARRITO_CADUCADO);
			this.propietario.setCarritoActual(null);
		}
	}

	public String getIdCarrito() {
		return this.idCarrito;
	}

	public List<LineaCarrito> getLineas() {
		return new ArrayList<>(this.lineas);
	}

	public LocalDateTime getFechaCreacion() {
		return this.fechaCreacion;
	}

	public Descuento getDescuentoAplicado() {
		return this.descuentoAplicado;
	}

	public void setDescuentoAplicado(Descuento descuento) {
		this.descuentoAplicado = descuento;
	}

	public boolean estaVacio() {
		return this.lineas.isEmpty();
	}

	public Cliente getPropietario() {
		return propietario;
	}
	public void imprimirCarrito() {
	    if (lineas.isEmpty()) {
	        System.out.println("  Carrito vacio.");
	        return;
	    }
	    System.out.println("  Carrito [" + idCarrito + "]:");
	    for (LineaCarrito l : lineas) {
	        System.out.println("   -> " + l.getProducto().getNombre()
	            + " x" + l.getCantidad()
	            + " | " + l.getProducto().getPrecioOficial() + "€/ud"
	            + " | subtotal: " + l.getSubtotal() + "€");
	    }
	    System.out.println("  Subtotal: " + calcularSubtotal() + "€");
	    System.out.println("  Descuento: " + (descuentoAplicado != null
	        ? descuentoAplicado.getNombre() : "ninguno"));
	    System.out.println("  Total: " + getTotal() + "€");
	}
}