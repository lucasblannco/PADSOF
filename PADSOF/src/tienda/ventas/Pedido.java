package ventas;

import java.time.*;
import java.util.*;

import productos.ProductoVenta;
import tienda.Estadistica;
import tienda.Tienda;
import tienda.TipoNotificacion;
import usuarios.Cliente;

public class Pedido {
	private boolean recogidaSolicitada;
	private final String idPedido;
	private final LocalDateTime fechaCreacion;
	private LocalDateTime fechaPreparado;
	private LocalDateTime fechaEntregado;

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

		Estadistica est = Estadistica.getInstancia();
		this.idPedido = "PEDIDO-" + String.valueOf(est.getnVentas());
		est.setnVentas(est.getnVentas() + 1);
		this.fechaCreacion = LocalDateTime.now();
		this.fechaPreparado = null;
		this.fechaEntregado = null;
		this.recogidaSolicitada = false;
		this.cliente = cliente;
		this.lineas = new ArrayList<>();
		this.pago = null;
		this.estado = EstadoPedido.PENDIENTE_PAGO;
		this.codigoRecogida = null;
		this.descuentoAplicado = carrito.getDescuentoAplicado();

		for (LineaCarrito linea : carrito.getLineas()) {
			ProductoVenta producto = linea.getProducto();
			int cantidad = linea.getCantidad();
			double precioUnitarioFijado = producto.getPrecioOficial();

			this.lineas.add(new LineaPedido(producto, cantidad, precioUnitarioFijado));
		}

		this.total = recalcularTotal(carrito);

		if (carrito.getDescuentoAplicado() instanceof Regalo) {
			Regalo regalo = (Regalo) carrito.getDescuentoAplicado();
			if (regalo.aplicaRegalo(carrito)) {
				ProductoVenta prod = regalo.getProductoRegalo();
				this.lineas.add(new LineaPedido(prod, 1, 0.0));
				prod.setStockDisponible(prod.getStockDisponible() - 1);
			}
		}
	}

	private double recalcularTotal(Carrito carrito) {
		double suma = 0.0;

		for (LineaPedido linea : this.lineas) {
			suma += linea.getSubtotal();
		}

		if (this.descuentoAplicado != null && carrito !=null) {
			suma = this.descuentoAplicado.aplicarDescuento(carrito);
		}

		return suma;
	}

	public boolean pagar(String tarjeta, int cvv, Date caducidad) {
		if (isCaducado()) {
			return false;
		}
		if (this.estado != EstadoPedido.PENDIENTE_PAGO) {
			System.out.println("El pedido no está pendiente de pago");
			return false;
		}

		Pago nuevoPago = new Pago(tarjeta, this.total, caducidad, cvv);
		this.pago = nuevoPago;

		if (this.pago.getExito()) {
			this.estado = EstadoPedido.PAGADO;
			this.codigoRecogida = "PICK-" + this.idPedido;
			this.cliente.recibirNotificacionTipo("Pago confirmado. Tu código de recogida es: " + this.codigoRecogida,
					TipoNotificacion.PAGO_EXITOSO);
			return true;
		}
		System.out.println("El pago no se ha podido procesar");
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
			return -1.0;
		}

		for (LineaPedido linea : this.lineas) {
			if (linea.getProducto().getId().equals(idProductoBuscado)) {
				if (linea.getCantidad() == 0) {
					return 0.0;
				}
				return linea.getPrecioVenta();
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
		this.total = recalcularTotal(null);
		return true;
	}

	public boolean isCaducado() {
		if (this.estado != EstadoPedido.PENDIENTE_PAGO) {
			return false;
		}
		return LocalDateTime.now().isAfter(this.fechaCreacion.plusMinutes(Tienda.getInstancia().getTiempoMaxPago()));
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

	public boolean isRecogida_solicitada() {
		return recogidaSolicitada;
	}

	public void setRecogida_solicitada(boolean recogida_solicitada) {
		this.recogidaSolicitada = recogida_solicitada;
	}
}