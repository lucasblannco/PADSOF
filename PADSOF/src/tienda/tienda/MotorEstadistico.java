package tienda;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import intercambios.*;
import usuarios.*;
import ventas.*;
import productos.*;

public class MotorEstadistico {

	public List<Cliente> obtenerClientesConMasPedidosCaducados() {
		Tienda tienda = Tienda.getInstancia();

		List<Cliente> clientes = tienda.obtenerClientesTienda();

		if (clientes.isEmpty()) {
			return new ArrayList<>();
		}
		// sort hace q ordenemos la lista
		// Comparator.comparingInt indica q debemos ordenarlos segun un int, q
		// calculamos despues de ->
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
		// lo mismo
		clientes.sort(Comparator.comparingInt((Cliente c) -> {
			int count = 0;
			for (Pedido p : c.getHistorialPedidos()) {
				if (p.getEstado() == EstadoPedido.PAGADO || p.getEstado() == EstadoPedido.LISTO_PARA_RECOGER
						|| p.getEstado() == EstadoPedido.ENTREGADO) {
					count++;
				}
			}
			return count;
		}).reversed()); // hacemos una lista ascendente

		return clientes;
	}

	public List<Cliente> obtenerClientesConMasIntercambios() {

		Tienda tienda = Tienda.getInstancia();

		List<Cliente> clientes = tienda.obtenerClientesTienda();

		if (clientes.isEmpty()) {
			return new ArrayList<>();
		}

		List<Oferta> intercambios = tienda.getIntercambiosFinalizados();

		// usams exactamente la misma logics
		clientes.sort(Comparator.comparingInt((Cliente c) -> {

			int count = 0;
			for (Oferta o : intercambios) {
				if (o.getOrigen().equals(c) || o.getDestino().equals(c)) {
					count++;
				}
			}

			return count;

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
		return (Estadistica.getInstancia().getnTasacionesCobradas()) * (Tienda.getInstancia().getPrecioTasacion());
	}

	private double calcularTasacionesEnRango(List<Producto2Mano> productos, LocalDate inicio, LocalDate fin) {
		double total = 0.0;
		for (Producto2Mano p : productos) {
			if (p.getValoracion() != null && p.getValoracion().getFecha() != null) {
				LocalDate fechaVal = p.getValoracion().getFecha().toLocalDate();
				if (!fechaVal.isBefore(inicio) && !fechaVal.isAfter(fin)) {
					total += p.getValoracion().getPrecioTasacion();
				}
			}
		}
		return total;
	}
}
