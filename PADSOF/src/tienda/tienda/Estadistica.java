package tienda;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import intercambios.EstadoOferta;
import intercambios.Oferta;
import usuarios.Cliente;
import usuarios.UsuarioRegistrado;
import ventas.EstadoPedido;
import ventas.Pedido;

public class Estadistica {

	private static Estadistica instancia;

	private Estadistica() {
	}

	public static Estadistica getInstancia() {
		if (instancia == null)
			instancia = new Estadistica();
		return instancia;
	}

	private int nProductosVentas = 1;
	private int nUsuarioRegistrado = 1;
	private int nUsuarioNoRegistrado = 1;
	private int nProducto2Mano = 1;
	private int nVentas = 1;
	private int nDescuentos = 1;
	private int nIntercambiosFinalizados = 1;
	private int nCategorias = 1;
	private int nCarritos = 1;
	private int nReseñas = 1;
	private int nTasacionesCobradas = 0;
	private int nNotificaciones = 1;

	public int getnProductosVentas() {
		return nProductosVentas;
	}

	public void setnProductosVentas(int n) {
		this.nProductosVentas = n;
	}

	public int getnUsuarioRegistrado() {
		return nUsuarioRegistrado;
	}

	public void setnUsuarioRegistrado(int n) {
		this.nUsuarioRegistrado = n;
	}

	public int getnUsuarioNoRegistrado() {
		return nUsuarioNoRegistrado;
	}

	public void setnUsuarioNoRegistrado(int n) {
		this.nUsuarioNoRegistrado = n;
	}

	public int getnProducto2Mano() {
		return nProducto2Mano;
	}

	public void setnProducto2Mano(int n) {
		this.nProducto2Mano = n;
	}

	public int getnVentas() {
		return nVentas;
	}

	public void setnVentas(int n) {
		this.nVentas = n;
	}

	public int getnDescuentos() {
		return nDescuentos;
	}

	public void setnDescuentos(int n) {
		this.nDescuentos = n;
	}

	public int getnIntercambiosFinalizados() {
		return nIntercambiosFinalizados;
	}

	public void setnIntercambiosFinalizados(int n) {
		this.nIntercambiosFinalizados = n;
	}

	public int getnCategorias() {
		return nCategorias;
	}

	public void setnCategorias(int n) {
		this.nCategorias = n;
	}

	public int getnCarritos() {
		return nCarritos;
	}

	public void setnCarritos(int n) {
		this.nCarritos = n;
	}

	public int getnReseñas() {
		return nReseñas;
	}

	public void setnReseñas(int n) {
		this.nReseñas = n;
	}

	public int getnTasacionesCobradas() {
		return nTasacionesCobradas;
	}

	public void setnTasacionesCobradas(int n) {
		this.nTasacionesCobradas = n;
	}

	public List<Cliente> obtenerClientesConMasPedidosCaducados() {
		Tienda tienda = Tienda.getInstancia();

		List<Cliente> clientes = tienda.obtenerClientesTienda();

		if (clientes.isEmpty()) {
			return new ArrayList<>();
		}

		clientes.sort(Comparator.comparingInt((Cliente c) -> {
			int count = 0;
			for (Pedido p : c.getHistorialPedidos()) {
				if (p.getEstado() == EstadoPedido.CANCELADO) {
					count++;
				}
			}
			return count;
		}).reversed());

		return clientes;
	}

	public List<Cliente> obtenerClientesConMasCompras() {
		Tienda tienda = Tienda.getInstancia();

		List<Cliente> clientes = tienda.obtenerClientesTienda();

		if (clientes.isEmpty()) {
			return new ArrayList<>();
		}

		clientes.sort(Comparator.comparingInt((Cliente c) -> {
			int count = 0;
			for (Pedido p : c.getHistorialPedidos()) {
				if (p.getEstado() == EstadoPedido.PAGADO || p.getEstado() == EstadoPedido.LISTO_PARA_RECOGER
						|| p.getEstado() == EstadoPedido.ENTREGADO) {
					count++;
				}
			}
			return count;
		}).reversed());

		return clientes;
	}

	/**
	 * Obtiene la lista de clientes ordenados por cantidad de intercambios
	 * finalizados.
	 * 
	 * @return Una lista de objetos Cliente ordenados de forma descendente.
	 */
	public List<Cliente> obtenerClientesConMasIntercambios() {

		Tienda tienda = Tienda.getInstancia();

		// Obtenemos la lista de todos los clientes de la tienda
		List<Cliente> clientes = tienda.obtenerClientesTienda();

		if (clientes.isEmpty()) {
			return new ArrayList<>();
		}

		List<Oferta> intercambios = tienda.getIntercambiosFinalizados();

		// Ordenamos la lista de clientes de mayor a menor número de intercambios
		// Comparator.comparingInt crea un comparador que compara clientes por un número
		// entero
		clientes.sort(Comparator.comparingInt((Cliente c) -> {
			// Para cada cliente contamos cuántos intercambios ha tenido
			int count = 0;
			for (Oferta o : intercambios) {
				if (o.getOrigen().equals(c) || o.getDestino().equals(c)) {
					count++;
				}
			}

			return count;
			// .reversed() invierte el orden para que el que más intercambios tenga salga
			// primero
		}).reversed());
		return clientes;
	}

	public double calcularIngresosRangoFechas(LocalDate inicio, LocalDate fin) {
		if (inicio == null || fin == null) {
			System.out.println("Las fechas de inicio y fin no pueden ser null.");
			return 0.0;
		}

		if (fin.isBefore(inicio)) {
			System.out.println("La fecha de fin no puede ser anterior a la de inicio.");
			return 0.0;
		}

		Tienda tienda = Tienda.getInstancia();
		if (tienda == null) {
			System.out.println("La tienda no ha sido inicializada.");
			return 0.0;
		}

		double total = 0.0;
		for (Pedido p : tienda.getHistorialVentas()) {

			if (p.getEstado() == EstadoPedido.PAGADO || p.getEstado() == EstadoPedido.LISTO_PARA_RECOGER
					|| p.getEstado() == EstadoPedido.ENTREGADO) {

				LocalDate fechaPedido = p.getFechaCreacion().toLocalDate();
				if (!fechaPedido.isBefore(inicio) && !fechaPedido.isAfter(fin)) {// Comprobamos que este dentro del
																					// rango de fechas
					total += p.getTotal();
				}
			}
		}
		return total;
	}

	public double[] calcularIngresosMeses(int año) {
		Tienda tienda = Tienda.getInstancia();
		double[] ingresosPorMes = new double[12];

		for (Pedido p : tienda.getHistorialVentas()) {
			if (p.getEstado() == EstadoPedido.PAGADO || p.getEstado() == EstadoPedido.LISTO_PARA_RECOGER
					|| p.getEstado() == EstadoPedido.ENTREGADO) {

				LocalDateTime fecha = p.getFechaCreacion();
				if (fecha != null && fecha.getYear() == año) {
					int mes = fecha.getMonthValue() - 1;
					ingresosPorMes[mes] += p.getTotal();
				}
			}
		}

		return ingresosPorMes;
	}

	public double calcularIngresosVenta() {

		Tienda tienda = Tienda.getInstancia();
		double total = 0.0;
		for (Pedido p : tienda.getHistorialVentas()) {
			if (p.getEstado() == EstadoPedido.PAGADO || p.getEstado() == EstadoPedido.LISTO_PARA_RECOGER
					|| p.getEstado() == EstadoPedido.ENTREGADO) {
				total += p.getTotal();
			}
		}
		return total;
	}

	public double calcularIngresosTasacion() {
		return (Estadistica.getInstancia().nTasacionesCobradas) * (Tienda.getInstancia().getPrecioTasacion());
	}

	public int getnNotificaciones() {
		return nNotificaciones;
	}

	public void setnNotificaciones(int nNotificaciones) {
		this.nNotificaciones = nNotificaciones;
	}
}