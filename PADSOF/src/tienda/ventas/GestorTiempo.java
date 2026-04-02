package ventas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import tienda.Tienda;
import usuarios.Cliente;

public class GestorTiempo {

	private final Map<String, Carrito> carritosPorUsuario;
	private final Map<String, List<Pedido>> pedidosPendientesPorUsuario;
	private final ScheduledExecutorService scheduler;

	public GestorTiempo() {
		this.carritosPorUsuario = new ConcurrentHashMap<>();
		this.pedidosPendientesPorUsuario = new ConcurrentHashMap<>();
		this.scheduler = Executors.newSingleThreadScheduledExecutor();

		iniciarRevisionPeriodica();
	}

	private void iniciarRevisionPeriodica() {
		Tienda tienda = Tienda.getInstancia();

		int tiempoCarrito = tienda.getTiempoMaxCarrito();
		int tiempoOferta = tienda.getTiempoMaxOferta();

		int tiempoRevision;

		if (tiempoCarrito > 0 && tiempoOferta > 0) {
			tiempoRevision = tiempoCarrito <= tiempoOferta ? tiempoCarrito : tiempoOferta;
		} else if (tiempoCarrito > 0) {
			tiempoRevision = tiempoCarrito;
		} else if (tiempoOferta > 0) {
			tiempoRevision = tiempoOferta;
		} else {
			tiempoRevision = 5;
		}

		if (tiempoRevision < 1) {
			tiempoRevision = 1;
		}

		this.scheduler.scheduleAtFixedRate(() -> {
			revisarCarritosCaducados();
			revisarPedidosPendientesCaducados();
		}, tiempoRevision, tiempoRevision, TimeUnit.MINUTES);
	}

	private void revisarCarritosCaducados() {
		Iterator<Map.Entry<String, Carrito>> it = carritosPorUsuario.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Carrito> entry = it.next();
			Carrito carrito = entry.getValue();
			if (carrito != null && carrito.estaCaducado()) {
				carrito.caducar();
				it.remove();
			}
		}
	}

	private void revisarPedidosPendientesCaducados() {
		Iterator<Map.Entry<String, List<Pedido>>> itMapa = pedidosPendientesPorUsuario.entrySet().iterator();
		while (itMapa.hasNext()) {
			Map.Entry<String, List<Pedido>> entry = itMapa.next();
			List<Pedido> pedidos = entry.getValue();

			if (pedidos == null) {
				itMapa.remove();
				continue;
			}

			Iterator<Pedido> it = pedidos.iterator();
			while (it.hasNext()) {
				Pedido pedido = it.next();
				if (pedido != null && pedido.isCaducado()) {
					pedido.cancelarPedido();
					it.remove();
				}
			}

			if (pedidos.isEmpty()) {
				itMapa.remove();
			}
		}
	}

	public Carrito obtenerCarrito(Cliente cliente) {
		if (cliente == null) {
			return null;
		}
		return carritosPorUsuario.get(cliente.getId());
	}

	public Carrito getCarrito(String idUsuario) {
		return carritosPorUsuario.get(idUsuario);
	}

	public void registrarCarrito(String idUsuario, Carrito carrito) {
		if (idUsuario == null || carrito == null) {
			throw new IllegalArgumentException("Ni idUsuario ni carrito pueden ser null");
		}

		carritosPorUsuario.put(idUsuario, carrito);
	}

	public void eliminarCarrito(String idUsuario) {
		Carrito carrito = carritosPorUsuario.remove(idUsuario);

		if (carrito != null) {
			carrito.caducar();
		}
	}

	public Pedido crearPedidoDesdeCarrito(Cliente cliente) {
		if (cliente == null)
			throw new IllegalArgumentException("cliente no puede ser null");

		String idUsuario = cliente.getId();
		Carrito carrito = carritosPorUsuario.get(idUsuario);

		if (carrito == null) {
			throw new IllegalStateException("El usuario no tiene carrito");
		}

		if (carrito.estaCaducado()) {
			carrito.caducar();
			carritosPorUsuario.remove(idUsuario, carrito);
			throw new IllegalStateException("El carrito ha caducado");
		}

		if (carrito.estaVacio()) {
			throw new IllegalStateException("El carrito está vacío");
		}

		Pedido pedido = new Pedido(cliente, carrito);
		carritosPorUsuario.remove(idUsuario, carrito);
		pedidosPendientesPorUsuario.computeIfAbsent(idUsuario, k -> new ArrayList<>()).add(pedido);

		return pedido;
	}

	public boolean pagarPedidoPendiente(String idUsuario, String idPedido, String tarjeta, int cvv,
			java.util.Date caducidad) {
		List<Pedido> pedidos = pedidosPendientesPorUsuario.get(idUsuario);

		if (pedidos == null || pedidos.isEmpty()) {
			return false;
		}

		Iterator<Pedido> it = pedidos.iterator();
		while (it.hasNext()) {
			Pedido pedido = it.next();

			if (pedido.getIdPedido().equals(idPedido)) {
				boolean pagado = pedido.pagar(tarjeta, cvv, caducidad);

				if (pagado) {
					it.remove();
					if (pedidos.isEmpty()) {
						pedidosPendientesPorUsuario.remove(idUsuario);
					}
				}

				return pagado;
			}
		}

		return false;
	}

	public boolean cancelarPedidoPendiente(String idUsuario, String idPedido) {
		List<Pedido> pedidos = pedidosPendientesPorUsuario.get(idUsuario);

		if (pedidos == null || pedidos.isEmpty()) {
			return false;
		}

		Iterator<Pedido> it = pedidos.iterator();
		while (it.hasNext()) {
			Pedido pedido = it.next();

			if (pedido.getIdPedido().equals(idPedido)) {
				boolean cancelado = pedido.cancelarPedido();

				if (cancelado) {
					it.remove();
					if (pedidos.isEmpty()) {
						pedidosPendientesPorUsuario.remove(idUsuario);
					}
				}

				return cancelado;
			}
		}

		return false;
	}

	public List<Pedido> getPedidosPendientesDeUsuario(String idUsuario) {
		List<Pedido> pedidos = pedidosPendientesPorUsuario.get(idUsuario);

		if (pedidos == null) {
			return new ArrayList<>();
		}

		return new ArrayList<>(pedidos);
	}

	public void cerrarGestorTiempo() {
		scheduler.shutdown();
	}
}
