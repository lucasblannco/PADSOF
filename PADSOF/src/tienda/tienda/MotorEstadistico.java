package tienda;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import Excepcion.AnioInvalidoException;
import Excepcion.RangoFechasInvalidoException;
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

	public double calcularIngresosRangoFechas(LocalDate inicio, LocalDate fin) throws RangoFechasInvalidoException {
		if (inicio == null || fin == null || fin.isBefore(inicio)) {
			throw new RangoFechasInvalidoException(inicio, fin);
		}

		Tienda tienda = Tienda.getInstancia();
		if (tienda == null) {
			System.out.println("La tienda no ha sido inicializada.");
			return 0.0;
		} // tenemos en cuenta los tasados y los no tasados
		return this.calcularIngresosVentaRango(inicio, fin) + this.calcularTasacionesEnRango(inicio, fin);
	}

	public double[] calcularIngresosMesesAño(int año) throws AnioInvalidoException, RangoFechasInvalidoException {
		double[] ingresosPorMes = new double[12];
		if (año <= 0) {
			throw new AnioInvalidoException(año);
		}

		for (int mes = 1; mes <= 12; mes++) {
			YearMonth ym = YearMonth.of(año, mes);// devuelve el mes en la posicion mes del año año
			LocalDate inicio = ym.atDay(1);// primer dia
			LocalDate fin = ym.atEndOfMonth();// ultimo dia
			ingresosPorMes[mes - 1] = this.calcularIngresosRangoFechas(inicio, fin);
		}

		return ingresosPorMes;
	}

	public double[] calcularIngresosMesesAñoActual() throws AnioInvalidoException, RangoFechasInvalidoException {
		return this.calcularIngresosMesesAño(LocalDate.now().getYear());
	}

	public double calcularIngresosVentaRango(LocalDate inicio, LocalDate fin) throws RangoFechasInvalidoException {
		if (inicio == null || fin == null || fin.isBefore(inicio)) {
			throw new RangoFechasInvalidoException(inicio, fin);
		}

		Tienda tienda = Tienda.getInstancia();
		if (tienda == null) {
			System.out.println("La tienda no ha sido inicializada.");
		}

		double total = 0.0;
		for (Pedido p : tienda.getHistorialVentas()) {
			if (p.getEstado() == EstadoPedido.CANCELADO)
				continue;// confirmamos q el pedido se realizo
			LocalDate fechaPedido = p.getFechaCreacion().toLocalDate();
			if (!fechaPedido.isBefore(inicio) && !fechaPedido.isAfter(fin)) {
				total += p.getTotal();
			}
		}
		return total;
	}

	public double calcularIngresosVenta() {
		try {
			return calcularIngresosVentaRango(LocalDate.MIN, LocalDate.MAX);
		} catch (RangoFechasInvalidoException e) {
			return 0.0;
		}
	}

	public double calcularIngresosTasacion() {
		return (Estadistica.getInstancia().getnTasacionesCobradas()) * (Tienda.getInstancia().getPrecioTasacion());
	}

	private double calcularTasacionesEnRango(LocalDate inicio, LocalDate fin) {
		double total = 0.0;
		for (Producto2Mano p : Tienda.getInstancia().getHistorialProductos2Mano()) {
			if (p.getValoracion() != null && p.getValoracion().getFecha() != null) {
				LocalDate fechaVal = p.getValoracion().getFecha().toLocalDate();
				if (!fechaVal.isBefore(inicio) && !fechaVal.isAfter(fin)) {
					total += p.getValoracion().getPrecioPagado();
				}
			}
		}
		return total;
	}
}