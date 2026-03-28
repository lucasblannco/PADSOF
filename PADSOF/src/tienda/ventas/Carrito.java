package ventas;

import java.time.*;
import java.util.*;
import tienda.Estadistica;
import tienda.Tienda;
import productos.ProductoVenta;

public class Carrito {
	private final String idCarrito;
	private final List<LineaCarrito> lineas;
	private final LocalDateTime fechaCreacion;
	private Descuento descuentoAplicado;
		

	public Carrito() {
		Estadistica est = Estadistica.getInstancia();
		this.idCarrito = "CARRITO" + String.valueOf(est.getnCarritos());
		est.setnCarritos(est.getnCarritos() + 1);
		this.lineas = new ArrayList<>();
		this.fechaCreacion = LocalDateTime.now();
		this.descuentoAplicado = null;
	}
//No tiene mucho sentido crear un carrito ya con descuento aplicado. El descuento se aplica cuando se va a pagar, no al crear el carrito.???????
	public Carrito(Descuento descuentoAplicado) {
		Estadistica est = Estadistica.getInstancia();
		this.idCarrito = "CARRITO" + String.valueOf(est.getnCarritos());
		est.setnCarritos(est.getnCarritos() + 1);
		this.lineas = new ArrayList<>();
		this.fechaCreacion = LocalDateTime.now();
		this.descuentoAplicado = descuentoAplicado;
	}

	public boolean añadirProducto(ProductoVenta p, int cantidad) {
		if (this.estaCaducado() == true) {
			this.vaciarCarrito();
			return false;
		}

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
		if (this.estaCaducado() == true) {
			this.vaciarCarrito();
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
			return true;
		}

		return false;
	}

	public boolean cambiarCantidadProducto(ProductoVenta p, int nuevaCantidad) {
		if (this.estaCaducado() == true) {
			this.vaciarCarrito();
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
	    // Primero aplicamos el descuento prioritario de la tienda
	    Tienda.getInstancia().aplicarDescuentoPrioritario(this);
	    
	    if (this.descuentoAplicado != null) {
	        return this.descuentoAplicado.aplicarDescuento(this);
	    }
	    return calcularSubtotal();
	}

	public boolean estaCaducado() {
	    int tiempoMax = Tienda.getInstancia().getTiempoMaxCarrito();
	    if (tiempoMax == 0) return false;
	    return LocalDateTime.now().isAfter(this.fechaCreacion.plusMinutes(tiempoMax));
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


	public boolean caducar() {
		vaciarCarrito();
		return true;
	}
}