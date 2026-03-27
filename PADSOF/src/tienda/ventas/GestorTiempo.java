package ventas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
		this.scheduler.scheduleAtFixedRate(() -> {
			revisarCarritosCaducados();
			revisarPedidosPendientesCaducados();
		}, 5, 5, TimeUnit.MINUTES);
	}

	private void revisarCarritosCaducados() {
		for (Map.Entry<String, Carrito> entry : carritosPorUsuario.entrySet()) {
			String idUsuario = entry.getKey();
			Carrito carrito1 = entry.getValue();

			if (carrito1 != null && carrito1.estaCaducado()) {
				carrito1.caducar(); // devuelve stock y lo deja inutilizable
				carritosPorUsuario.remove(idUsuario, carrito1);
			}
		}
	}

	private void revisarPedidosPendientesCaducados() {
		for (Map.Entry<String, List<Pedido>> entry : pedidosPendientesPorUsuario.entrySet()) {
			String idUsuario = entry.getKey();
			List<Pedido> pedidos = entry.getValue();

			if (pedidos == null) {
				continue;
			}

			Iterator<Pedido> it = pedidos.iterator();
			while (it.hasNext()) {
				Pedido pedido = it.next();

				if (pedido != null && pedido.haSuperadoTiempoMaximoPendientePago()) {
					pedido.cancelarPedido(); // devuelve stock y pasa a CANCELADO
					it.remove();
				}
			}

			if (pedidos.isEmpty()) {
				pedidosPendientesPorUsuario.remove(idUsuario);
			}
		}
	}

	public Carrito obtenerOCrearCarrito(String idUsuario) {
		Carrito carrito = carritosPorUsuario.get(idUsuario);

		if (carrito != null) {
			return carrito;
		}

		Carrito nuevoCarrito = new Carrito();
		carritosPorUsuario.put(idUsuario, nuevoCarrito);
		return nuevoCarrito;
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
			carrito.cancelar();
		}
	}

	public Pedido crearPedidoDesdeCarrito(String idUsuario, Cliente cliente) {
		if (idUsuario == null || cliente == null) {
			throw new IllegalArgumentException("idUsuario y cliente no pueden ser null");
		}

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

		boolean confirmado = carrito.confirmarCompra();
		if (!confirmado) {
			throw new IllegalStateException("No se pudo confirmar el carrito");
		}

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
