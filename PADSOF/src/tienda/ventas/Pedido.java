package ventas;

import java.time.*;
import java.util.*;

import productos.ProductoVenta;
import usuarios.Cliente;

public class Pedido {

	private final String idPedido;
	private final LocalDateTime fechaCreacion;
	private LocalDateTime fechaPreparado;
	private LocalDateTime fechaEntregado;
	private static Duration tiempoMaximo;

	private final Cliente cliente;
	private final List<LineaPedido> lineas;

	private Pago pago;
	private double total;
	private EstadoPedido estado;
	private String codigoRecogida;
	private Descuento descuentoAplicado;

	public Pedido(Cliente cliente, Carrito carrito) {
		if (cliente == null) {
			throw new IllegalArgumentException("El cliente no puede ser null");
		}

		if (carrito == null) {
			throw new IllegalArgumentException("El carrito no puede ser null");
		}

		if (carrito.estaCaducado()) {
			throw new IllegalArgumentException("No se puede crear un pedido desde un carrito caducado");
		}

		if (carrito.estaVacio()) {
			throw new IllegalArgumentException("No se puede crear un pedido con un carrito vacío");
		}

		this.idPedido = "ORDER-" + UUID.randomUUID().toString().substring(0, 8);
		this.fechaCreacion = LocalDateTime.now();
		this.fechaPreparado = null;
		this.fechaEntregado = null;

		this.cliente = cliente;
		this.lineas = new ArrayList<>();
		this.pago = null;
		this.estado = EstadoPedido.PENDIENTE_PAGO;
		this.codigoRecogida = null;
		this.descuentoAplicado = null;

		for (LineaCarrito linea : carrito.getLineas()) {
			ProductoVenta producto = linea.getProducto();
			int cantidad = linea.getCantidad();
			double precioUnitarioFijado = producto.getPrecioVenta();

			this.lineas.add(new LineaPedido(producto, cantidad, precioUnitarioFijado));
		}

		this.total = recalcularTotal();
	}

	private double recalcularTotal() {
		double suma = 0.0;

		for (LineaPedido linea : this.lineas) {
			suma += linea.getSubtotal(); 
		}

		if (this.descuentoAplicado != null) {
			suma = this.descuentoAplicado.aplicarDescuento(suma);
		}

		return suma;
	}

	public boolean pagar(String tarjeta, int cvv, Date caducidad) {
		if (this.estado != EstadoPedido.PENDIENTE_PAGO) {
			return false;
		}

		Pago nuevoPago = new Pago(tarjeta, this.total, caducidad, cvv);
		this.pago = nuevoPago;

		if (this.pago.getExito()) {
			this.estado = EstadoPedido.PAGADO;
			this.codigoRecogida = "PICK-" + this.idPedido;
			return true;
		}

		return false;
	}

	public boolean marcarPreparado() {
	    if (this.estado != EstadoPedido.PAGADO) {
	        return false;
	    }

	    this.estado = EstadoPedido.LISTO_PARA_RECOGER;
	    this.fechaPreparado = LocalDateTime.now();
	    return true;
	}

	public boolean marcarEntregado() {
		if (this.estado != EstadoPedido.LISTO_PARA_RECOGER) {
			return false;
		}

		this.estado = EstadoPedido.ENTREGADO;
		this.fechaEntregado = LocalDateTime.now();
		return true;
	}

	public boolean cancelarPedido() {
		if (this.estado == EstadoPedido.CANCELADO || this.estado == EstadoPedido.ENTREGADO) {
			return false;
		}

		for (LineaPedido linea : this.lineas) {
			ProductoVenta producto = linea.getProducto();
			producto.setStockDisponible(producto.getStockDisponible() + linea.getCantidad());
		}

		this.estado = EstadoPedido.CANCELADO;
		this.codigoRecogida = null;

		return true;
	}

	public boolean actualizarEstado(EstadoPedido nuevoEstado) {
		if (nuevoEstado == null) {
			return false;
		}

		if (nuevoEstado == this.estado) {
			return true;
		}

		switch (nuevoEstado) {
		case LISTO_PARA_RECOGER:
			return marcarPreparado();
		case ENTREGADO:
			return marcarEntregado();
		case CANCELADO:
			return cancelarPedido();
		default:
			return false;
		}
	}

	public boolean productoPertenece(ProductoVenta p) {
		if (p == null) {
			return false;
		}

		for (LineaPedido l : this.lineas) {
			if (l.productoPertenece(p)) {
				return true;
			}
		}

		return false;
	}

	public int contarUnidadesDe(String idProductoBuscado) {
		if (idProductoBuscado == null) {
			return 0;
		}

		int totalUnidades = 0;

		for (LineaPedido linea : this.lineas) {
			if (linea.getProducto().getId().equals(idProductoBuscado)) {
				totalUnidades += linea.getCantidad();
			}
		}

		return totalUnidades;
	}

	public double getPrecioDeProducto(String idProductoBuscado) {
		if (idProductoBuscado == null) {
			return 0.0;
		}

		for (LineaPedido linea : this.lineas) {
			if (linea.getProducto().getId().equals(idProductoBuscado)) {
				if (linea.getCantidad() == 0) {
					return 0.0;
				}
				return linea.getSubtotal() / linea.getCantidad();
			}
		}

		return 0.0;
	}

	public double getTotalBruto() {
		double totalBruto = 0.0;

		for (LineaPedido linea : this.lineas) {
			totalBruto += linea.getSubtotal();
		}

		return totalBruto;
	}

	public boolean setDescuentoAplicado(Descuento descuentoAplicado) {
	    if (this.estado != EstadoPedido.PENDIENTE_PAGO) {
	        return false;
	    }

	    this.descuentoAplicado = descuentoAplicado;
	    this.total = recalcularTotal();
	    return true;
	}
	
	public boolean isCaducado() {
		if (this.estado != EstadoPedido.PENDIENTE_PAGO) {
			return false;
		}

		if (Pedido.tiempoMaximo == null) {
			return false;
		}

		return LocalDateTime.now().isAfter(this.fechaCreacion.plus(Pedido.tiempoMaximo));
	}

	public String getIdPedido() {
		return this.idPedido;
	}

	public LocalDateTime getFechaCreacion() {
		return this.fechaCreacion;
	}

	public LocalDateTime getFechaPreparado() {
		return this.fechaPreparado;
	}

	public LocalDateTime getFechaEntregado() {
		return this.fechaEntregado;
	}

	public Cliente getCliente() {
		return this.cliente;
	}

	public List<LineaPedido> getLineas() {
		return new ArrayList<>(this.lineas);
	}

	public Pago getPago() {
		return this.pago;
	}

	public double getTotal() {
		return this.total;
	}

	public EstadoPedido getEstado() {
		return this.estado;
	}

	public String getCodigoRecogida() {
		return this.codigoRecogida;
	}

	public Descuento getDescuentoAplicado() {
		return this.descuentoAplicado;
	}
	
	public static void setTiempoMaximo(Duration tiempo) {
		if (tiempo == null || tiempo.isZero() || tiempo.isNegative()) {
			throw new IllegalArgumentException("El tiempo máximo del pedido debe ser positivo");
		}
		Pedido.tiempoMaximo = tiempo;
	}
}