package ventas;

import java.time.LocalDateTime;
import java.util.*;

import productos.ProductoVenta;

public class Carrito {
	private final String idCarrito;
	private final List<LineaCarrito> lineas;
	private final LocalDateTime fechaCreacion;
	private Descuento descuentoAplicado;

	public Carrito() {
		this.idCarrito = UUID.randomUUID().toString().substring(0, 8);
		this.lineas = new ArrayList<>();
		this.fechaCreacion = LocalDateTime.now();
		this.descuentoAplicado = null;
	}

	public Carrito(Descuento descuentoAplicado) {
		this.idCarrito = UUID.randomUUID().toString().substring(0, 8);
		this.lineas = new ArrayList<>();
		this.fechaCreacion = LocalDateTime.now();
		this.descuentoAplicado = descuentoAplicado;
	}

	/* esto esta bien */
	public boolean añadirProducto(ProductoVenta p, int cantidad) {
		if (p == null || cantidad < 1 || p.getStockDisponible() < cantidad) {
			return false;
		}

		for (LineaCarrito l : this.lineas) {
			if (l.productoPertence(p)) {
				l.setCantidad(l.getCantidad() + cantidad);
				p.setStockDisponible(p.getStockDisponible() - cantidad);
				return true;
			}
		}

		LineaCarrito nuevaLinea = new LineaCarrito(p, cantidad);
		this.lineas.add(nuevaLinea);
		p.setStockDisponible(p.getStockDisponible() - cantidad);
		return true;
	}

	public boolean eliminarProducto(ProductoVenta p) {
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
			return true;
		}

		return false;
	}

	public boolean cambiarCantidadProducto(ProductoVenta p, int nuevaCantidad) {
	    if (p == null || nuevaCantidad < 0) {
	        return false;
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
	}

	public double calcularSubtotal() {
		double suma = 0;

		for (LineaCarrito l : this.lineas) {
			suma += l.getSubtotal();
		}

		return suma;
	}

	public double getTotal() {
		double total = calcularSubtotal();

		if (this.descuentoAplicado != null) {
			total = this.descuentoAplicado.aplicarDescuento(total);
		}

		return total;
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

	public void setDescuentoAplicado(Descuento descuentoAplicado) {
		this.descuentoAplicado = descuentoAplicado;
	}

	public boolean estaVacio() {
		return this.lineas.isEmpty();
	}
}