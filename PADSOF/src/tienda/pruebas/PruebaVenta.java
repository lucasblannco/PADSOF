package pruebas;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import productos.Categoria;
import productos.*;
import tienda.Tienda;
import usuarios.Cliente;
import usuarios.Gestor;
import ventas.*;

/**
 * Clase de pruebas manuales del paquete ventas. Cubre: Carrito, Pedido,
 * Descuentos, Caducidad y Estados de pedido.
 *
 * Cómo ejecutar: lanza el método main() directamente. Cada bloque imprime PASS
 * o FAIL con una descripción del caso probado.
 */
public class PruebaVenta {

	// ----------------------------------------------------------------
	// Utilidades de prueba
	// ----------------------------------------------------------------

	private static int totalPruebas = 0;
	private static int pruebas_ok = 0;
	private static int pruebas_fail = 0;

	private static void check(String descripcion, boolean condicion) {
		totalPruebas++;
		if (condicion) {
			pruebas_ok++;
			System.out.println("  [PASS] " + descripcion);
		} else {
			pruebas_fail++;
			System.out.println("  [FAIL] " + descripcion);
		}
	}

	private static void titulo(String s) {
		System.out.println("\n========================================");
		System.out.println("  " + s);
		System.out.println("========================================");
	}

	// ----------------------------------------------------------------
	// Setup compartido: tienda, gestor, cliente y productos
	// ----------------------------------------------------------------

	private static Tienda tienda;
	private static Gestor gestor;
	private static Cliente cliente;
	private static ProductoVenta productoA; // precio 20€, stock 10
	private static ProductoVenta productoB; // precio 50€, stock 5
	private static Categoria categoriaJuegos;

	private static void setup() {
		// Reseteamos la instancia singleton para que cada ejecución parta limpia
		Tienda.setInstancia(null);
		tienda = Tienda.getInstancia();

		gestor = tienda.getGestor();

		// Configurar tiempos del sistema (necesario para que el cliente pueda comprar)
		gestor.setTiemposSistema(60, 60, 60); // oferta=60min, carrito=60min, pago=60min

		// Registrar cliente
		cliente = tienda.registrarNuevoCliente("clienteTest", "Pass@1234", "12345678A");

		// Crear categoría
		gestor.crearCategoria("Juegos", "Videojuegos de todas las plataformas");
		categoriaJuegos = tienda.buscarCategoriaPorNombre("Juegos");

		// Crear productos de venta
		productoA = new Comic("Zelda", "Juego de aventuras", "imagen", 20.0, 10, 150, "editorial", 2026);
		productoB = new JuegoMesa("FIFA", "Juego de fútbol", "imagen2", 50.0, 5, 2, 8, 3, 99, "tipoJuego");
		tienda.añadirProducto(productoA);
		tienda.añadirProducto(productoB);

		// Asociar productoA a la categoría Juegos
		gestor.añadirProductoACategoria(productoA.getId(), "Juegos");
	}

	private static void forzarFechaCreacionCarrito(Carrito carrito, LocalDateTime nuevaFecha) {
		try {
			Field f = Carrito.class.getDeclaredField("fechaCreacion");
			f.setAccessible(true);
			f.set(carrito, nuevaFecha);
		} catch (Exception e) {
			throw new RuntimeException("No se pudo forzar la fecha de creación del carrito", e);
		}
	}

	private static void forzarFechaCreacionPedido(Pedido pedido, LocalDateTime nuevaFecha) {
		try {
			Field f = Pedido.class.getDeclaredField("fechaCreacion");
			f.setAccessible(true);
			f.set(pedido, nuevaFecha);
		} catch (Exception e) {
			throw new RuntimeException("No se pudo forzar la fecha de creación del pedido", e);
		}
	}

	private static void forzarFechaCreacionCarrito(Carrito carrito, LocalDateTime nuevaFecha) {
		try {
			Field f = Carrito.class.getDeclaredField("fechaCreacion");
			f.setAccessible(true);
			f.set(carrito, nuevaFecha);
		} catch (Exception e) {
			throw new RuntimeException("No se pudo forzar la fecha de creación del carrito", e);
		}
	}

	private static void forzarFechaCreacionPedido(Pedido pedido, LocalDateTime nuevaFecha) {
		try {
			Field f = Pedido.class.getDeclaredField("fechaCreacion");
			f.setAccessible(true);
			f.set(pedido, nuevaFecha);
		} catch (Exception e) {
			throw new RuntimeException("No se pudo forzar la fecha de creación del pedido", e);
		}
	}

	private static void invocarRevisionCarritosCaducados(GestorTiempo gt) {
		try {
			Method m = GestorTiempo.class.getDeclaredMethod("revisarCarritosCaducados");
			m.setAccessible(true);
			m.invoke(gt);
		} catch (Exception e) {
			throw new RuntimeException("No se pudo invocar revisarCarritosCaducados()", e);
		}
	}

	private static void invocarRevisionPedidosCaducados(GestorTiempo gt) {
		try {
			Method m = GestorTiempo.class.getDeclaredMethod("revisarPedidosPendientesCaducados");
			m.setAccessible(true);
			m.invoke(gt);
		} catch (Exception e) {
			throw new RuntimeException("No se pudo invocar revisarPedidosPendientesCaducados()", e);
		}
	}

	// ----------------------------------------------------------------
	// BLOQUE 1 — Operaciones básicas del carrito
	// ----------------------------------------------------------------

	private static void probarCarritoBasico() {
		titulo("BLOQUE 1 — Carrito: operaciones básicas");

		// Añadir producto
		boolean añadido = cliente.añadirProductoCarrito(productoA, 2);
		check("Añadir 2 unidades de productoA al carrito", añadido);
		check("Stock de productoA baja a 8 tras añadir 2", productoA.getStockDisponible() == 8);
		check("Carrito no está vacío", !cliente.getCarritoActual().estaVacio());

		// Añadir más unidades del mismo producto (debe acumularse en la misma línea)
		cliente.añadirProductoCarrito(productoA, 1);
		check("Añadir 1 más: sólo hay 1 línea en el carrito", cliente.getCarritoActual().getLineas().size() == 1);
		check("Cantidad de la línea es 3", cliente.getCarritoActual().getLineas().get(0).getCantidad() == 3);

		// Añadir segundo producto
		cliente.añadirProductoCarrito(productoB, 2);
		check("Hay 2 líneas en el carrito", cliente.getCarritoActual().getLineas().size() == 2);

		// Subtotal: 3*20 + 2*50 = 160
		check("Subtotal correcto (160€)", cliente.getCarritoActual().calcularSubtotal() == 160.0);

		// Cambiar cantidad de productoA a 1
		boolean cambiado = cliente.getCarritoActual().cambiarCantidadProducto(productoA, 1);
		check("Cambiar cantidad de productoA a 1", cambiado);
		check("Stock de productoA vuelve a 9 (se liberaron 2)", productoA.getStockDisponible() == 9);

		// Eliminar productoB
		boolean eliminado = cliente.getCarritoActual().eliminarProducto(productoB);
		check("Eliminar productoB del carrito", eliminado);
		check("Stock de productoB vuelve a 5", productoB.getStockDisponible() == 5);
		check("Ahora hay 1 línea en el carrito", cliente.getCarritoActual().getLineas().size() == 1);

		// Vaciar carrito
		cliente.getCarritoActual().vaciarCarrito();
		check("Carrito vacío tras vaciar", cliente.getCarritoActual().estaVacio());
		check("Stock de productoA vuelve a 10 al vaciar", productoA.getStockDisponible() == 10);
	}

	// ----------------------------------------------------------------
	// BLOQUE 2 — Validaciones y casos límite del carrito
	// ----------------------------------------------------------------

	private static void probarCarritoValidaciones() {
		titulo("BLOQUE 2 — Carrito: validaciones y casos límite");

		// Añadir producto null
		boolean res = cliente.añadirProductoCarrito(null, 1);
		check("No se puede añadir producto null", !res);

		// Añadir cantidad 0 o negativa
		res = cliente.añadirProductoCarrito(productoA, 0);
		check("No se puede añadir cantidad 0", !res);

		res = cliente.añadirProductoCarrito(productoA, -1);
		check("No se puede añadir cantidad negativa", !res);

		// Añadir más stock del disponible
		res = cliente.añadirProductoCarrito(productoA, 999);
		check("No se puede añadir más del stock disponible (999 > 10)", !res);

		// Cambiar cantidad a 0 equivale a eliminar
		cliente.añadirProductoCarrito(productoA, 2);
		cliente.getCarritoActual().cambiarCantidadProducto(productoA, 0);
		check("Cambiar cantidad a 0 elimina el producto del carrito", cliente.getCarritoActual().estaVacio());
		check("Stock vuelve a 10 al eliminar con cantidad 0", productoA.getStockDisponible() == 10);

		// Eliminar producto que no está en el carrito
		boolean eliminado = cliente.getCarritoActual().eliminarProducto(productoB);
		check("Eliminar producto que no está en el carrito devuelve false", !eliminado);
	}

	// ----------------------------------------------------------------
	// BLOQUE 3 — Creación de pedido y estados
	// ----------------------------------------------------------------

	private static void probarPedidoYEstados() {
		titulo("BLOQUE 3 — Pedido: creación y transición de estados");

		// Preparar carrito
		cliente.añadirProductoCarrito(productoA, 2); // 2 * 20 = 40€
		Carrito carrito = cliente.getCarritoActual();
		check("Carrito listo para reservar", !carrito.estaVacio());

		// Reservar (crear pedido)
		boolean reservado = cliente.reservarCarrito();
		check("Reserva del carrito exitosa", reservado);
		check("Carrito se limpia tras reservar", cliente.getCarritoActual() == null);
		check("Historial del cliente tiene 1 pedido", cliente.getHistorialPedidos().size() == 1);

		Pedido pedido = cliente.getHistorialPedidos().get(0);
		check("Estado inicial del pedido es PENDIENTE_PAGO", pedido.getEstado() == EstadoPedido.PENDIENTE_PAGO);
		check("Total del pedido es 40€", pedido.getTotal() == 40.0);

		// No se puede pasar directamente a ENTREGADO desde PENDIENTE_PAGO
		boolean saltado = pedido.actualizarEstado(EstadoPedido.ENTREGADO);
		check("No se puede saltar a ENTREGADO desde PENDIENTE_PAGO", !saltado);

		// Pagar (usamos tarjeta y datos de prueba; el banco externo puede fallar,
		// así que comprobamos el comportamiento del sistema, no el resultado del banco)
		Date caducidadValida = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365);
		pedido.pagar("1234567890123456", 123, caducidadValida);
		// Sea cual sea el resultado del banco, el estado sólo cambia si el pago tuvo
		// éxito
		if (pedido.getEstado() == EstadoPedido.PAGADO) {
			check("Si el banco acepta, estado pasa a PAGADO", true);

			// Marcar preparado
			boolean preparado = pedido.actualizarEstado(EstadoPedido.LISTO_PARA_RECOGER);
			check("Estado pasa a LISTO_PARA_RECOGER", preparado);
			check("Fecha de preparado se registra", pedido.getFechaPreparado() != null);

			// No se puede cancelar si ya está listo para recoger... depende de la lógica;
			// en tu código cancelarPedido permite cancelar desde cualquier estado excepto
			// CANCELADO o ENTREGADO, así que sí se puede.
			// Marcar entregado
			boolean entregado = pedido.actualizarEstado(EstadoPedido.ENTREGADO);
			check("Estado pasa a ENTREGADO", entregado);
			check("Fecha de entregado se registra", pedido.getFechaEntregado() != null);

			// No se puede volver a cancelar un pedido entregado
			boolean cancelado = pedido.cancelarPedido();
			check("No se puede cancelar un pedido ya ENTREGADO", !cancelado);

		} else {
			// El banco rechazó (entorno de pruebas sin conexión real): verificamos
			// que el estado NO cambió
			check("Si el banco rechaza, el estado sigue en PENDIENTE_PAGO",
					pedido.getEstado() == EstadoPedido.PENDIENTE_PAGO);
		}
	}

	// ----------------------------------------------------------------
	// BLOQUE 4 — Cancelación de pedido y devolución de stock
	// ----------------------------------------------------------------

	private static void probarCancelacionPedido() {
		titulo("BLOQUE 4 — Pedido: cancelación y devolución de stock");

		int stockAntes = productoA.getStockDisponible();
		cliente.añadirProductoCarrito(productoA, 3);
		cliente.reservarCarrito();

		Pedido pedido = cliente.getHistorialPedidos().get(cliente.getHistorialPedidos().size() - 1);
		check("Stock baja 3 al crear el pedido", productoA.getStockDisponible() == stockAntes - 3);

		boolean cancelado = pedido.cancelarPedido();
		check("Cancelar pedido devuelve true", cancelado);
		check("Estado es CANCELADO", pedido.getEstado() == EstadoPedido.CANCELADO);
		check("Stock de productoA vuelve al nivel anterior", productoA.getStockDisponible() == stockAntes);
		check("Código de recogida se borra al cancelar", pedido.getCodigoRecogida() == null);

		// Cancelar dos veces no debe hacer nada
		boolean dobleCancelacion = pedido.cancelarPedido();
		check("No se puede cancelar un pedido ya CANCELADO", !dobleCancelacion);
	}

	// ----------------------------------------------------------------
	// BLOQUE 5 — Descuento por volumen
	// ----------------------------------------------------------------

	private static void probarDescuentoVolumen() {
		titulo("BLOQUE 5 — Descuento por volumen");

		LocalDateTime inicio = LocalDateTime.now().minusMinutes(1);
		LocalDateTime fin = LocalDateTime.now().plusHours(2);

		// Umbral 50€, 10% de descuento
		DescuentoVolumen dv = new DescuentoVolumen("Volumen10", inicio, fin, 50.0, 10.0);
		tienda.agregarDescuento(dv);

		// Carrito por debajo del umbral: 1 * 20 = 20€ → sin descuento
		cliente.añadirProductoCarrito(productoA, 1);
		Carrito carrito = cliente.getCarritoActual();
		double totalSinDescuento = carrito.getTotal();
		check("Sin alcanzar umbral, getTotal() = subtotal (20€)", totalSinDescuento == 20.0);

		// Añadir más para superar el umbral: total = 3 * 20 = 60€ → 60 * 0.9 = 54€
		cliente.añadirProductoCarrito(productoA, 2);
		double totalConDescuento = carrito.getTotal();
		check("Superando umbral (60€), descuento del 10% aplicado → 54€", Math.abs(totalConDescuento - 54.0) < 0.01);

		carrito.vaciarCarrito();
		tienda.getDescuentosActivos().clear();
	}

	// ----------------------------------------------------------------
	// BLOQUE 6 — Descuento por categoría
	// ----------------------------------------------------------------

	private static void probarDescuentoCategoria() {
		titulo("BLOQUE 6 — Descuento por categoría");

		LocalDateTime inicio = LocalDateTime.now().minusMinutes(1);
		LocalDateTime fin = LocalDateTime.now().plusHours(2);

		// 20% de descuento en categoría Juegos (productoA pertenece a Juegos)
		DescuentoCategoria dc = new DescuentoCategoria("CatJuegos20", inicio, fin, categoriaJuegos, 20.0);
		tienda.agregarDescuento(dc);

		// Añadir productoA (Juegos, 20€) y productoB (sin categoría, 50€)
		cliente.añadirProductoCarrito(productoA, 1); // 20 * 0.8 = 16€
		cliente.añadirProductoCarrito(productoB, 1); // 50€ sin descuento
		// Total esperado: 16 + 50 = 66€
		double total = cliente.getCarritoActual().getTotal();
		check("Descuento categoría: productoA con 20% → total 66€", Math.abs(total - 66.0) < 0.01);

		cliente.getCarritoActual().vaciarCarrito();
		tienda.getDescuentosActivos().clear();
	}

	// ----------------------------------------------------------------
	// BLOQUE 7 — Descuento por cantidad
	// ----------------------------------------------------------------

	private static void probarDescuentoCantidad() {
		titulo("BLOQUE 7 — Descuento por cantidad");

		LocalDateTime inicio = LocalDateTime.now().minusMinutes(1);
		LocalDateTime fin = LocalDateTime.now().plusHours(2);

		// A partir de 3 unidades, 25% de descuento sobre toda la línea
		DescuentoCantidad dca = new DescuentoCantidad("Cant3=25%", inicio, fin, 3, 25.0);
		tienda.agregarDescuento(dca);

		// Con 2 unidades NO llega al mínimo → sin descuento: 2 * 20 = 40€
		cliente.añadirProductoCarrito(productoA, 2);
		double sinDescuento = cliente.getCarritoActual().getTotal();
		check("Con 2 unidades (< mínimo 3), no hay descuento → 40€", Math.abs(sinDescuento - 40.0) < 0.01);

		// Añadir 1 más → 3 unidades, sí aplica: 3 * 20 * 0.75 = 45€
		cliente.añadirProductoCarrito(productoA, 1);
		double conDescuento = cliente.getCarritoActual().getTotal();
		check("Con 3 unidades (= mínimo), descuento 25% → 45€", Math.abs(conDescuento - 45.0) < 0.01);

		cliente.getCarritoActual().vaciarCarrito();
		tienda.getDescuentosActivos().clear();
	}

	// ----------------------------------------------------------------
	// BLOQUE 8 — Solo se aplica un descuento (el prioritario)
	// ----------------------------------------------------------------

	private static void probarUnSoloDescuento() {
		titulo("BLOQUE 8 — Solo se aplica 1 descuento (el prioritario = el primero que aplica)");

		LocalDateTime inicio = LocalDateTime.now().minusMinutes(1);
		LocalDateTime fin = LocalDateTime.now().plusHours(2);

		// Descuento 1 (primero en la lista): volumen 30€ → 5%
		DescuentoVolumen dv = new DescuentoVolumen("Volumen5", inicio, fin, 30.0, 5.0);
		// Descuento 2 (segundo): categoría Juegos → 50% (mejor para el cliente)
		DescuentoCategoria dc = new DescuentoCategoria("CatJuegos50", inicio, fin, categoriaJuegos, 50.0);

		tienda.agregarDescuento(dv);
		tienda.agregarDescuento(dc);

		// Añadir productoA (Juegos, 20€) * 2 = 40€ → ambos descuentos aplican
		// Opción B: se aplica el primero que aplica (dv): 40 * 0.95 = 38€
		cliente.añadirProductoCarrito(productoA, 2);
		double total = cliente.getCarritoActual().getTotal();
		check("Con dos descuentos activos, sólo se aplica el primero (volumen 5%) → 38€",
				Math.abs(total - 38.0) < 0.01);

		// El descuentoAplicado en el carrito debe ser dv, no dc
		check("El descuento aplicado es el de volumen (primero en lista)",
				cliente.getCarritoActual().getDescuentoAplicado() == dv);

		cliente.getCarritoActual().vaciarCarrito();
		tienda.getDescuentosActivos().clear();
	}

	// ----------------------------------------------------------------
	// BLOQUE 9 — Descuento caducado no se aplica
	// ----------------------------------------------------------------

	private static void probarDescuentoCaducado() {
		titulo("BLOQUE 9 — Descuento caducado no se aplica");

		// Fechas en el pasado → descuento inactivo
		LocalDateTime inicio = LocalDateTime.now().minusHours(2);
		LocalDateTime fin = LocalDateTime.now().minusHours(1);

		DescuentoVolumen dvCaducado = new DescuentoVolumen("Caducado", inicio, fin, 10.0, 50.0);
		tienda.agregarDescuento(dvCaducado);

		cliente.añadirProductoCarrito(productoA, 2); // 40€
		double total = cliente.getCarritoActual().getTotal();
		check("Descuento caducado no se aplica → total sigue siendo 40€", Math.abs(total - 40.0) < 0.01);
		check("Descuento aplicado en carrito es null", cliente.getCarritoActual().getDescuentoAplicado() == null);

		cliente.getCarritoActual().vaciarCarrito();
		tienda.getDescuentosActivos().clear();
	}

	// ----------------------------------------------------------------
	// BLOQUE 10 — Descuento se recalcula al modificar el carrito
	// ----------------------------------------------------------------

	private static void probarRecalculoDescuento() {
		titulo("BLOQUE 10 — Descuento se recalcula al modificar el carrito");

		LocalDateTime inicio = LocalDateTime.now().minusMinutes(1);
		LocalDateTime fin = LocalDateTime.now().plusHours(2);

		// Umbral 50€, 20% descuento
		DescuentoVolumen dv = new DescuentoVolumen("Volumen20", inicio, fin, 50.0, 20.0);
		tienda.agregarDescuento(dv);

		// Añadir productoA * 2 = 40€ → NO supera umbral
		cliente.añadirProductoCarrito(productoA, 2);
		check("Con 40€ no aplica descuento", cliente.getCarritoActual().getDescuentoAplicado() == null);

		// Añadir productoB * 1 = 40 + 50 = 90€ → SÍ supera umbral → 90 * 0.8 = 72€
		cliente.añadirProductoCarrito(productoB, 1);
		check("Al superar 50€, descuento se aplica automáticamente",
				cliente.getCarritoActual().getDescuentoAplicado() == dv);
		check("Total con descuento es 72€", Math.abs(cliente.getCarritoActual().getTotal() - 72.0) < 0.01);

		// Eliminar productoB → volvemos a 40€ → descuento desaparece
		cliente.getCarritoActual().eliminarProducto(productoB);
		check("Al bajar de 50€ eliminando producto, descuento desaparece",
				cliente.getCarritoActual().getDescuentoAplicado() == null);

		cliente.getCarritoActual().vaciarCarrito();
		tienda.getDescuentosActivos().clear();
	}

	// ----------------------------------------------------------------
	// BLOQUE 11 — Caducidad del carrito
	// ----------------------------------------------------------------

	private static void probarCaducidadCarrito() {
		titulo("BLOQUE 11 — Caducidad del carrito");

		// Configurar tiempo de carrito a 0 (sin caducidad)
		tienda.setTiempoMaxCarrito(0);
		cliente.añadirProductoCarrito(productoA, 1);
		check("Con tiempoMaxCarrito=0 el carrito nunca caduca", !cliente.getCarritoActual().estaCaducado());
		cliente.getCarritoActual().vaciarCarrito();

		// Comprobar que estaCaducado() devuelve false cuando aún no ha expirado
		tienda.setTiempoMaxCarrito(60); // 60 minutos
		cliente.añadirProductoCarrito(productoA, 1);
		check("Carrito recién creado no está caducado", !cliente.getCarritoActual().estaCaducado());
		cliente.getCarritoActual().vaciarCarrito();

		// No podemos simular paso del tiempo sin manipular fechas (campo final),
		// pero sí podemos verificar el método caducar() directamente
		cliente.añadirProductoCarrito(productoA, 2);
		int stockAntes = productoA.getStockDisponible();
		Carrito carritoACaducar = cliente.getCarritoActual(); // guardamos la referencia
		carritoACaducar.caducar();
		check("Al caducar, el carrito queda vacío", carritoACaducar.estaVacio());
		check("Al caducar, el stock se devuelve", productoA.getStockDisponible() == stockAntes + 2);
		// Restaurar tiempo normal
		tienda.setTiempoMaxCarrito(60);
	}

	// ----------------------------------------------------------------
	// BLOQUE 12 — Caducidad del pedido (isCaducado)
	// ----------------------------------------------------------------

	private static void probarCaducidadPedido() {
		titulo("BLOQUE 12 — Caducidad del pedido");

		cliente.añadirProductoCarrito(productoA, 1);
		cliente.reservarCarrito();
		Pedido pedido = cliente.getHistorialPedidos().get(cliente.getHistorialPedidos().size() - 1);

		// Con tiempoMaxPago=60, el pedido recién creado no está caducado
		check("Pedido recién creado no está caducado", !pedido.isCaducado());

		// Un pedido ya pagado (no PENDIENTE_PAGO) nunca se considera caducado
		// Forzamos el estado internamente probando con un pedido cancelado
		pedido.cancelarPedido();
		check("Un pedido CANCELADO nunca está caducado (isCaducado=false)", !pedido.isCaducado());
	}

	// ----------------------------------------------------------------
	// BLOQUE 13 — Regalo
	// ----------------------------------------------------------------

	private static void probarRegalo() {
		titulo("BLOQUE 13 — Descuento tipo Regalo");

		LocalDateTime inicio = LocalDateTime.now().minusMinutes(1);
		LocalDateTime fin = LocalDateTime.now().plusHours(2);

		// Regalo: al gastar más de 35€ te regalan productoB (50€, stock 5)
		int stockRegaloBefore = productoB.getStockDisponible();
		Regalo regalo = new Regalo("RegaloFIFA", inicio, fin, 35.0, productoB);
		tienda.agregarDescuento(regalo);

		// Carrito con 40€ → supera umbral → aplica regalo
		cliente.añadirProductoCarrito(productoA, 2); // 40€
		check("Regalo aplica cuando se supera el umbral (40€ > 35€)", regalo.aplicaRegalo(cliente.getCarritoActual()));
		check("getTotal() no modifica el precio (el regalo no tiene descuento económico)",
				Math.abs(cliente.getCarritoActual().getTotal() - 40.0) < 0.01);

		// Al reservar, la línea del regalo se añade al pedido con precio 0
		cliente.reservarCarrito();
		Pedido pedido = cliente.getHistorialPedidos().get(cliente.getHistorialPedidos().size() - 1);
		boolean tieneLineaRegalo = false;
		for (LineaPedido lp : pedido.getLineas()) {
			if (lp.getProducto() == productoB && lp.getPrecioVenta() == 0.0) {
				tieneLineaRegalo = true;
			}
		}
		check("Pedido contiene línea de regalo con precio 0", tieneLineaRegalo);
		check("Stock del producto regalo bajó en 1", productoB.getStockDisponible() == stockRegaloBefore - 1);

		tienda.getDescuentosActivos().clear();
	}

	// ----------------------------------------------------------------
	// BLOQUE 14 — Descuento en pedido con recalcularTotal sin carrito
	// ----------------------------------------------------------------

	private static void probarRecalcularTotalSinCarrito() {
		titulo("BLOQUE 14 — Pedido: recalcularTotal cuando no hay carrito (setDescuentoAplicado)");

		cliente.añadirProductoCarrito(productoA, 3); // 3 * 20 = 60€
		cliente.reservarCarrito();
		Pedido pedido = cliente.getHistorialPedidos().get(cliente.getHistorialPedidos().size() - 1);

		LocalDateTime inicio = LocalDateTime.now().minusMinutes(1);
		LocalDateTime fin = LocalDateTime.now().plusHours(2);

		// Aplicar un DescuentoVolumen directamente al pedido (sin carrito)
		DescuentoVolumen dv = new DescuentoVolumen("VolPedido10", inicio, fin, 50.0, 10.0);
		boolean aplicado = pedido.setDescuentoAplicado(dv);
		check("setDescuentoAplicado devuelve true en estado PENDIENTE_PAGO", aplicado);
		check("Total del pedido baja un 10% (60 * 0.9 = 54€)", Math.abs(pedido.getTotal() - 54.0) < 0.01);

		// Intentar cambiar descuento en un pedido ya cancelado
		pedido.cancelarPedido();
		boolean aplicadoTarde = pedido.setDescuentoAplicado(dv);
		check("No se puede cambiar descuento en pedido CANCELADO", !aplicadoTarde);
	}

	// ----------------------------------------------------------------
	// BLOQUE 15 — Regalo: casos borde y stock
	// ----------------------------------------------------------------

	private static void probarRegaloCasosBorde() {
		titulo("BLOQUE 15 — Regalo: casos borde y stock");

		LocalDateTime inicio = LocalDateTime.now().minusMinutes(1);
		LocalDateTime fin = LocalDateTime.now().plusHours(2);

		// ---------- Caso 1: cancelar pedido con regalo devuelve también el stock del
		// regalo ----------
		tienda.getDescuentosActivos().clear();

		int stockAAntes = productoA.getStockDisponible();
		int stockBAntes = productoB.getStockDisponible();

		Regalo regalo = new Regalo("RegaloCancelacion", inicio, fin, 35.0, productoB);
		tienda.agregarDescuento(regalo);

		cliente.añadirProductoCarrito(productoA, 2); // 40€ -> aplica regalo
		boolean reservado = cliente.reservarCarrito();
		check("Reserva con regalo exitosa", reservado);

		Pedido pedido = cliente.getHistorialPedidos().get(cliente.getHistorialPedidos().size() - 1);
		check("El stock del regalo baja al crear pedido", productoB.getStockDisponible() == stockBAntes - 1);

		boolean cancelado = pedido.cancelarPedido();
		check("Cancelar pedido con regalo devuelve true", cancelado);
		check("Cancelar pedido devuelve stock del producto comprado", productoA.getStockDisponible() == stockAAntes);
		check("Cancelar pedido devuelve también stock del regalo", productoB.getStockDisponible() == stockBAntes);

		// ---------- Caso 2: el regalo no altera el total del pedido ----------
		tienda.getDescuentosActivos().clear();

		Regalo regalo2 = new Regalo("RegaloTotal", inicio, fin, 35.0, productoB);
		tienda.agregarDescuento(regalo2);

		cliente.añadirProductoCarrito(productoA, 2); // 40€
		cliente.reservarCarrito();
		Pedido pedido2 = cliente.getHistorialPedidos().get(cliente.getHistorialPedidos().size() - 1);

		check("Pedido con regalo mantiene total económico de 40€", Math.abs(pedido2.getTotal() - 40.0) < 0.01);

		// ---------- Caso 3: si el producto regalo ya estaba comprado, debe haber línea
		// pagada y línea regalo ----------
		tienda.getDescuentosActivos().clear();
		pedido2.cancelarPedido(); // limpiamos stock del caso anterior

		Regalo regalo3 = new Regalo("RegaloMismoProducto", inicio, fin, 35.0, productoB);
		tienda.agregarDescuento(regalo3);

		cliente.añadirProductoCarrito(productoA, 2); // 40€
		cliente.añadirProductoCarrito(productoB, 1); // además compra el mismo producto que será regalo
		cliente.reservarCarrito();
		Pedido pedido3 = cliente.getHistorialPedidos().get(cliente.getHistorialPedidos().size() - 1);

		int lineasProductoB = 0;
		boolean tieneLineaPagada = false;
		boolean tieneLineaRegalo = false;

		for (LineaPedido lp : pedido3.getLineas()) {
			if (lp.getProducto() == productoB) {
				lineasProductoB++;
				if (lp.getPrecioVenta() == productoB.getPrecioOficial()) {
					tieneLineaPagada = true;
				}
				if (lp.getPrecioVenta() == 0.0) {
					tieneLineaRegalo = true;
				}
			}
		}

		check("Si el regalo ya estaba comprado, existe línea pagada de productoB", tieneLineaPagada);
		check("Si el regalo ya estaba comprado, existe línea regalo separada de productoB", tieneLineaRegalo);
		check("Hay al menos 2 líneas de productoB (pagada + regalo)", lineasProductoB >= 2);

		pedido3.cancelarPedido();
		tienda.getDescuentosActivos().clear();
	}

	// ----------------------------------------------------------------
	// BLOQUE 16 — Casos borde: reservar, pago, stock exacto y limpieza
	// ----------------------------------------------------------------

	private static void probarCasosBordeGenerales() {
		titulo("BLOQUE 16 — Casos borde: reservar, pago, stock exacto y limpieza");

		// ---------- Caso 1: reservar con carrito null ----------
		cliente.setCarritoActual(null);
		boolean reservadoNull = cliente.reservarCarrito();
		check("No se puede reservar si carritoActual es null", !reservadoNull);

		// ---------- Caso 2: reservar carrito vacío ----------
		cliente.setCarritoActual(new Carrito(cliente));
		boolean reservadoVacio = cliente.reservarCarrito();
		check("No se puede reservar un carrito vacío", !reservadoVacio);

		// ---------- Caso 3: añadir exactamente el stock disponible ----------
		cliente.setCarritoActual(null);
		int stockInicialA = productoA.getStockDisponible();

		boolean añadirExacto = cliente.añadirProductoCarrito(productoA, stockInicialA);
		check("Añadir exactamente el stock disponible sí se puede", añadirExacto);
		check("El stock queda a 0 tras añadir exactamente todo", productoA.getStockDisponible() == 0);

		boolean añadirUnoMas = cliente.añadirProductoCarrito(productoA, 1);
		check("No se puede añadir una unidad más cuando el stock es 0", !añadirUnoMas);

		cliente.getCarritoActual().vaciarCarrito();
		check("Vaciar carrito restaura todo el stock original", productoA.getStockDisponible() == stockInicialA);

		// ---------- Caso 4: vaciar carrito limpia descuento aplicado ----------
		LocalDateTime inicio = LocalDateTime.now().minusMinutes(1);
		LocalDateTime fin = LocalDateTime.now().plusHours(2);

		DescuentoVolumen dv = new DescuentoVolumen("VolumenVaciar", inicio, fin, 30.0, 10.0);
		tienda.agregarDescuento(dv);

		cliente.añadirProductoCarrito(productoA, 2); // 40€, aplica descuento
		check("Antes de vaciar, hay descuento aplicado", cliente.getCarritoActual().getDescuentoAplicado() != null);

		cliente.getCarritoActual().vaciarCarrito();
		check("Tras vaciar, el carrito queda vacío", cliente.getCarritoActual().estaVacio());
		check("Tras vaciar, el descuento aplicado queda en null",
				cliente.getCarritoActual().getDescuentoAplicado() == null);

		tienda.getDescuentosActivos().clear();

		// ---------- Caso 5: pagar pedido cancelado ----------
		cliente.añadirProductoCarrito(productoA, 1);
		cliente.reservarCarrito();
		Pedido pedido = cliente.getHistorialPedidos().get(cliente.getHistorialPedidos().size() - 1);

		pedido.cancelarPedido();
		Date caducidadValida = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365);
		boolean pagoCancelado = pedido.pagar("1234567890123456", 123, caducidadValida);
		check("No se puede pagar un pedido cancelado", !pagoCancelado);

		// ---------- Caso 6: pagar dos veces el mismo pedido ----------
		cliente.añadirProductoCarrito(productoA, 1);
		cliente.reservarCarrito();
		Pedido pedido2 = cliente.getHistorialPedidos().get(cliente.getHistorialPedidos().size() - 1);

		boolean primerPago = pedido2.pagar("1234567890123456", 123, caducidadValida);

		if (primerPago) {
			boolean segundoPago = pedido2.pagar("1234567890123456", 123, caducidadValida);
			check("No se puede pagar dos veces el mismo pedido", !segundoPago);
		} else {
			check("Si el primer pago falla, el pedido sigue pendiente",
					pedido2.getEstado() == EstadoPedido.PENDIENTE_PAGO);
		}

		// ---------- Caso 7: actualizarEstado(null) devuelve false ----------
		cliente.añadirProductoCarrito(productoA, 1);
		cliente.reservarCarrito();
		Pedido pedido3 = cliente.getHistorialPedidos().get(cliente.getHistorialPedidos().size() - 1);

		boolean estadoNull = pedido3.actualizarEstado(null);
		check("actualizarEstado(null) devuelve false", !estadoNull);
	}

	// ----------------------------------------------------------------
	// BLOQUE 17 — Caducidad real: carrito y pedido por paso del tiempo
	// ----------------------------------------------------------------

	private static void probarCaducidadRealPorTiempo() {
		titulo("BLOQUE 17 — Caducidad real: carrito y pedido por paso del tiempo");

		// ---------- Caso 1: carrito realmente caducado ----------
		tienda.setTiempoMaxCarrito(60);
		cliente.setCarritoActual(null);

		int stockAntes = productoA.getStockDisponible();
		cliente.añadirProductoCarrito(productoA, 2);
		Carrito carrito = cliente.getCarritoActual();

		forzarFechaCreacionCarrito(carrito, LocalDateTime.now().minusMinutes(61));

		check("Carrito forzado a más de 60 min aparece como caducado", carrito.estaCaducado());

		// Al intentar operar, debe caducar y liberar stock
		boolean añadirEnCaducado = carrito.añadirProducto(productoA, 1);
		check("No se puede operar sobre un carrito ya caducado", !añadirEnCaducado);
		check("El carrito caducado queda vacío", carrito.estaVacio());
		check("El stock se libera al detectar caducidad del carrito", productoA.getStockDisponible() == stockAntes);
		check("El cliente pierde la referencia al carrito caducado", cliente.getCarritoActual() == null);

		// ---------- Caso 2: pedido realmente caducado ----------
		tienda.setTiempoMaxPago(60);
		cliente.añadirProductoCarrito(productoA, 1);
		cliente.reservarCarrito();
		Pedido pedido = cliente.getHistorialPedidos().get(cliente.getHistorialPedidos().size() - 1);

		forzarFechaCreacionPedido(pedido, LocalDateTime.now().minusMinutes(61));

		check("Pedido forzado a más de 60 min aparece como caducado", pedido.isCaducado());

		Date caducidadValida = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365);
		boolean pagadoCaducado = pedido.pagar("1234567890123456", 123, caducidadValida);
		check("No se puede pagar un pedido ya caducado", !pagadoCaducado);
		check("El pedido caducado sigue en PENDIENTE_PAGO", pedido.getEstado() == EstadoPedido.PENDIENTE_PAGO);

		// ---------- Caso 3: justo antes del límite no debe caducar ----------
		cliente.añadirProductoCarrito(productoA, 1);
		cliente.reservarCarrito();
		Pedido pedido2 = cliente.getHistorialPedidos().get(cliente.getHistorialPedidos().size() - 1);

		forzarFechaCreacionPedido(pedido2, LocalDateTime.now().minusMinutes(59));

		check("Pedido con 59 min no está caducado", !pedido2.isCaducado());

		cliente.setCarritoActual(null);
		cliente.añadirProductoCarrito(productoA, 1);
		Carrito carrito2 = cliente.getCarritoActual();

		forzarFechaCreacionCarrito(carrito2, LocalDateTime.now().minusMinutes(59));

		check("Carrito con 59 min no está caducado", !carrito2.estaCaducado());
	}

	// ----------------------------------------------------------------
	// BLOQUE 18 — GestorTiempo: registro, pedido, pago, cancelación y limpieza
	// ----------------------------------------------------------------

	private static void probarGestorTiempo() {
		titulo("BLOQUE 18 — GestorTiempo: registro, pedido, pago, cancelación y limpieza");

		GestorTiempo gt = new GestorTiempo();

		try {
			// Datos propios de este bloque para no depender del estado global
			Cliente clienteGT = tienda.registrarNuevoCliente("clienteGT", "Pass@1234", "99999999Z");
			ProductoVenta productoGT = new Comic("ProductoGT", "Producto para pruebas GestorTiempo", "img", 15.0, 10,
					100, "editorialGT", 2026);

			tienda.añadirProducto(productoGT);

			// ---------- Caso 1: registrar y obtener carrito ----------
			clienteGT.setCarritoActual(null);
			Carrito carrito = new Carrito(clienteGT);
			gt.registrarCarrito(clienteGT.getId(), carrito);

			check("obtenerCarrito(cliente) devuelve el carrito registrado", gt.obtenerCarrito(clienteGT) == carrito);
			check("getCarrito(idUsuario) devuelve el carrito registrado", gt.getCarrito(clienteGT.getId()) == carrito);

			// ---------- Caso 2: eliminarCarrito lo borra y además lo caduca ----------
			int stockAntes = productoGT.getStockDisponible();
			boolean añadido = carrito.añadirProducto(productoGT, 2);

			check("Añadir producto al carrito del gestor devuelve true", añadido);
			check("Añadir al carrito del gestor baja stock en 2", productoGT.getStockDisponible() == stockAntes - 2);

			gt.eliminarCarrito(clienteGT.getId());

			check("eliminarCarrito borra el carrito del mapa", gt.getCarrito(clienteGT.getId()) == null);
			check("eliminarCarrito vacía el carrito", carrito.estaVacio());
			check("eliminarCarrito devuelve el stock al producto", productoGT.getStockDisponible() == stockAntes);

			// ---------- Caso 3: crearPedidoDesdeCarrito con carrito registrado ----------
			Carrito carrito2 = new Carrito(clienteGT);
			boolean añadido2 = carrito2.añadirProducto(productoGT, 1);
			check("Añadir producto al segundo carrito devuelve true", añadido2);

			gt.registrarCarrito(clienteGT.getId(), carrito2);

			int nPedidosAntes = clienteGT.getHistorialPedidos().size();
			Pedido pedido = gt.crearPedidoDesdeCarrito(clienteGT);

			check("crearPedidoDesdeCarrito devuelve un pedido no null", pedido != null);
			check("El carrito se elimina del gestor al crear pedido", gt.getCarrito(clienteGT.getId()) == null);
			check("El pedido creado queda pendiente en el gestor",
					gt.getPedidosPendientesDeUsuario(clienteGT.getId()).contains(pedido));
			check("El historial del cliente no disminuye", clienteGT.getHistorialPedidos().size() >= nPedidosAntes);

			// ---------- Caso 4: cancelarPedidoPendiente elimina el pedido pendiente
			// ----------
			boolean cancelado = gt.cancelarPedidoPendiente(clienteGT.getId(), pedido.getIdPedido());
			check("cancelarPedidoPendiente devuelve true para un pedido existente", cancelado);
			check("El pedido cancelado ya no aparece entre pendientes",
					!gt.getPedidosPendientesDeUsuario(clienteGT.getId()).contains(pedido));

			// ---------- Caso 5: cancelar un pedido inexistente devuelve false ----------
			boolean canceladoInexistente = gt.cancelarPedidoPendiente(clienteGT.getId(), "PEDIDO-INEXISTENTE");
			check("cancelarPedidoPendiente devuelve false si el id no existe", !canceladoInexistente);

			// ---------- Caso 6: pagarPedidoPendiente elimina el pedido de pendientes si
			// paga ----------
			Carrito carrito3 = new Carrito(clienteGT);
			boolean añadido3 = carrito3.añadirProducto(productoGT, 1);
			check("Añadir producto al tercer carrito devuelve true", añadido3);

			gt.registrarCarrito(clienteGT.getId(), carrito3);

			Pedido pedido2 = gt.crearPedidoDesdeCarrito(clienteGT);

			Date caducidadValida = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365);
			boolean pagado = gt.pagarPedidoPendiente(clienteGT.getId(), pedido2.getIdPedido(), "1234567890123456", 123,
					caducidadValida);

			if (pagado) {
				check("pagarPedidoPendiente devuelve true si el pago tiene éxito", true);
				check("El pedido pagado ya no queda entre pendientes",
						!gt.getPedidosPendientesDeUsuario(clienteGT.getId()).contains(pedido2));
			} else {
				check("Si el pago falla, el pedido sigue pendiente",
						gt.getPedidosPendientesDeUsuario(clienteGT.getId()).contains(pedido2));
			}

			// ---------- Caso 7: pagar pedido inexistente devuelve false ----------
			boolean pagoInexistente = gt.pagarPedidoPendiente(clienteGT.getId(), "PEDIDO-INEXISTENTE",
					"1234567890123456", 123, caducidadValida);
			check("pagarPedidoPendiente devuelve false si el id no existe", !pagoInexistente);

			// ---------- Caso 8: getPedidosPendientesDeUsuario devuelve copia defensiva
			// ----------
			List<Pedido> copia = gt.getPedidosPendientesDeUsuario(clienteGT.getId());
			int tamAntes = gt.getPedidosPendientesDeUsuario(clienteGT.getId()).size();
			copia.clear();
			check("Modificar la lista devuelta no altera la interna",
					gt.getPedidosPendientesDeUsuario(clienteGT.getId()).size() == tamAntes);

		} finally {
			gt.cerrarGestorTiempo();
		}
	}
	
	// ----------------------------------------------------------------
	// BLOQUE 19 — GestorTiempo: caducidad real de carritos y pedidos
	// ----------------------------------------------------------------

	private static void probarGestorTiempoCaducidades() {
		titulo("BLOQUE 19 — GestorTiempo: caducidad real de carritos y pedidos");

		GestorTiempo gt = new GestorTiempo();

		try {
			// Datos propios para aislar el bloque
			Cliente clienteGT2 = tienda.registrarNuevoCliente("clienteGT2", "Pass@1234", "88888888Z");
			ProductoVenta productoGT2 = new Comic("ProductoGT2", "Producto pruebas caducidad gestor", "img", 12.0, 10,
					100, "editorialGT2", 2026);
			tienda.añadirProducto(productoGT2);

			tienda.setTiempoMaxCarrito(60);
			tienda.setTiempoMaxPago(60);

			// ---------- Caso 1: el gestor elimina carritos caducados ----------
			Carrito carrito = new Carrito(clienteGT2);
			clienteGT2.setCarritoActual(carrito);
			gt.registrarCarrito(clienteGT2.getId(), carrito);

			int stockAntesCarrito = productoGT2.getStockDisponible();
			boolean añadido = carrito.añadirProducto(productoGT2, 2);

			check("Añadir al carrito registrado para probar caducidad devuelve true", añadido);
			check("Añadir al carrito registrado baja stock en 2", productoGT2.getStockDisponible() == stockAntesCarrito - 2);

			forzarFechaCreacionCarrito(carrito, LocalDateTime.now().minusMinutes(61));
			check("El carrito forzado aparece como caducado", carrito.estaCaducado());

			invocarRevisionCarritosCaducados(gt);

			check("La revisión elimina el carrito caducado del gestor", gt.getCarrito(clienteGT2.getId()) == null);
			check("La revisión vacía el carrito caducado", carrito.estaVacio());
			check("La revisión devuelve el stock del carrito caducado", productoGT2.getStockDisponible() == stockAntesCarrito);
			check("La revisión deja al cliente sin carrito actual", clienteGT2.getCarritoActual() == null);

			// ---------- Caso 2: el gestor cancela pedidos pendientes caducados ----------
			Carrito carrito2 = new Carrito(clienteGT2);
			clienteGT2.setCarritoActual(carrito2);
			boolean añadido2 = carrito2.añadirProducto(productoGT2, 3);

			check("Añadir al carrito para crear pedido pendiente devuelve true", añadido2);

			int stockAntesPedido = productoGT2.getStockDisponible();
			gt.registrarCarrito(clienteGT2.getId(), carrito2);

			Pedido pedido = gt.crearPedidoDesdeCarrito(clienteGT2);
			check("Crear pedido pendiente desde gestor devuelve pedido no null", pedido != null);
			check("El pedido recién creado figura entre pendientes",
					gt.getPedidosPendientesDeUsuario(clienteGT2.getId()).contains(pedido));

			forzarFechaCreacionPedido(pedido, LocalDateTime.now().minusMinutes(61));
			check("El pedido forzado aparece como caducado", pedido.isCaducado());

			invocarRevisionPedidosCaducados(gt);

			check("La revisión elimina el pedido caducado de pendientes",
					!gt.getPedidosPendientesDeUsuario(clienteGT2.getId()).contains(pedido));
			check("La revisión cancela el pedido caducado", pedido.getEstado() == EstadoPedido.CANCELADO);
			check("La revisión devuelve el stock del pedido cancelado por caducidad",
					productoGT2.getStockDisponible() == stockAntesPedido + 3);

			// ---------- Caso 3: si ya no quedan pendientes, la lista del usuario queda vacía ----------
			check("Cuando no quedan pendientes, el usuario devuelve lista vacía",
					gt.getPedidosPendientesDeUsuario(clienteGT2.getId()).isEmpty());

		} finally {
			gt.cerrarGestorTiempo();
		}
	}

	// ----------------------------------------------------------------
	// MAIN
	// ----------------------------------------------------------------

	public static void main(String[] args) {
		setup();

		probarCarritoBasico();
		probarCarritoValidaciones();
		probarPedidoYEstados();
		probarCancelacionPedido();
		probarDescuentoVolumen();
		probarDescuentoCategoria();
		probarDescuentoCantidad();
		probarUnSoloDescuento();
		probarDescuentoCaducado();
		probarRecalculoDescuento();
		probarCaducidadCarrito();
		probarCaducidadPedido();
		probarRegalo();
		probarRecalcularTotalSinCarrito();
		probarRegaloCasosBorde();
		probarCasosBordeGenerales();
		probarCaducidadRealPorTiempo();
		probarGestorTiempo();
		probarGestorTiempoCaducidades();

		System.out.println("\n========================================");
		System.out.println("  RESUMEN FINAL");
		System.out.println("========================================");
		System.out.println("  Total:  " + totalPruebas);
		System.out.println("  PASS:   " + pruebas_ok);
		System.out.println("  FAIL:   " + pruebas_fail);
		System.out.println("========================================");
	}
}