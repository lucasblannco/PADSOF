package pruebas;

import java.util.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import Excepcion.*;
import intercambios.*;
import productos.*;
import tienda.*;
import usuarios.*;
import ventas.*;

public class DemostradorMain {

	static void imprimirEmpleado(Empleado e) {
		System.out.println(
				" Empleado: " + e.getNickname() + " | id: " + e.getId() + " | permisos: " + e.getPermisos().size());
		for (TipoPermisos p : e.getPermisos()) {
			System.out.println("   -> " + p);
		}
	}

	static ArrayList<LineaPack> construirLineasPack(LineaPack... lineas) {
		ArrayList<LineaPack> lista = new ArrayList<>();
		for (LineaPack l : lineas) {
			lista.add(l);
		}
		return lista;
	}

	static void imprimirStockPack(Pack pack) {
		System.out.println("  Stock reservado para pack '" + pack.getNombre() + "':");
		for (LineaPack lp : pack.getLineas()) {
			System.out.println("   - " + lp.getProducto().getNombre() + " | unidades en pack: " + lp.getUnidades()
					+ " | stock restante: " + lp.getProducto().getStockDisponible());
		}
	}

	public static void main(String[] args) {
		System.out.println("Bienvenido al demostrado de la tienda CHECKPOINT");
		Tienda tienda = Tienda.getInstancia();
		Gestor gestor = tienda.getGestor();
		System.out.println("CONFIGURACION DE LOS TIEMPOS DE LA APLICACION");

		gestor.configurarTiemposSistema(60, 30, 30);
		System.out.println(" Tiempos configurados -> Carrito: " + tienda.getTiempoMaxCarrito() + "min | Oferta: "
				+ tienda.getTiempoMaxOferta() + "min | Pago: " + tienda.getTiempoMaxPago() + "min");

		gestor.setPrecioTasacion(10);
		System.out.println(" Precio de las valoraciones: " + tienda.getPrecioTasacion() + "€");

		System.out.println("\nCREACION DE CATEGORIAS");

		gestor.crearCategoria("Familiar", "Tematica para realizar en familia");
		gestor.crearCategoria("Accion", "Tematica de accion");
		gestor.crearCategoria("Ciencia-ficcion ", "Productos de ciencia ficcion");
		gestor.crearCategoria("Anime", "Productos de anime y manga");
		gestor.crearCategoria("Replicas", "Objetos de coleccionista a escala real");
		gestor.crearCategoria("Retro-Gaming",
				"Consolas clásicas, cartuchos originales y accesorios de sistemas de videojuegos antiguos.");

		String nombres = "";
		for (Categoria c : tienda.getCategorias()) {
			if (!nombres.equals("")) {
				nombres += ", ";
			}
			nombres += c.getNombre();
		}
		System.out.println("Categorias creadas: " + tienda.getCategorias().size() + " -> " + nombres);

		System.out.println("\nDAR DE ALTA EMPLEADOS EN LA TIENDA:");

		List<TipoPermisos> permisosStock = gestor.crearListaPermisos(TipoPermisos.GESTION_STOCK,
				TipoPermisos.GESTION_CATEGORIAS, TipoPermisos.GESTION_PACKS, TipoPermisos.MODIFICAR_PRODUCTO);
		gestor.darDeAltaEmpleados_Permisos("emp_stock", "Stock@1234", permisosStock);

		List<TipoPermisos> permisosTasador = gestor.crearListaPermisos(TipoPermisos.VALORACION_PRODUCTOS,
				TipoPermisos.CONFIRMACION_INTERCAMBIO);
		gestor.darDeAltaEmpleados_Permisos("emp_tasador", "Tasador@1234", permisosTasador);

		List<TipoPermisos> permisosPedidos = gestor.crearListaPermisos(TipoPermisos.GESTION_PEDIDOS,
				TipoPermisos.ENTREGA_PEDIDOS);
		gestor.darDeAltaEmpleados_Permisos("emp_pedidos", "Pedidos@1234", permisosPedidos);

		Empleado empStock = tienda.loginEmpleado("emp_stock", "Stock@1234");
		Empleado empTasador = tienda.loginEmpleado("emp_tasador", "Tasador@1234");
		Empleado empPedidos = tienda.loginEmpleado("emp_pedidos", "Pedidos@1234");

		System.out.println("RESUMEN EMPLEADOS:");
		imprimirEmpleado(empStock);
		imprimirEmpleado(empTasador);
		imprimirEmpleado(empPedidos);
		System.out.println(" Total empleados en tienda: " + tienda.obtenerEmpleadosTienda().size());

		System.out.println("\n CARGA DE PRODUCTOS:");
		System.out.println("Cargando productos desde fichero...");
		empStock.cargarProductosFicheroTexto("ficheros/productos.txt");
		System.out.println("  Productos tras fichero: " + tienda.getStockVentas().size());

		empStock.añadirProducto_nuevo("C", "Watchmen", "Clasico del comic", "watchmen.jpg", 15.00, 10,
				tienda.seleccionarCategorias("Accion", "Anime"), 400, "DC Comics", 1987, 0, 0, 0, null, null, 0, 0, 0,
				0, null);

		empStock.añadirProducto_nuevo("J", "Catan", "Juego de estrategia", "catan.jpg", 45.00, 8,
				tienda.seleccionarCategorias("Familiar", "Estrategia"), 0, null, 0, 0, 0, 0, null, null, 2, 4, 8, 99,
				"Estrategia");

		empStock.añadirProducto_nuevo("F", "Figura Goku SSJ", "Figura de Dragon Ball", "goku.jpg", 35.00, 5,
				tienda.seleccionarCategorias("Anime", "Replicas", "Accion"), 0, null, 0, 20.0, 15.0, 12.0, "PVC",
				"Bandai", 0, 0, 0, 0, null);

		empStock.añadirProducto_nuevo("C", "Akira Vol.1", "Manga de ciencia ficcion", "akira.jpg", 12.99, 20,
				tienda.seleccionarCategorias("Anime", "Ciencia-ficcion"), 350, "Kodansha", 1982, 0, 0, 0, null, null, 0,
				0, 0, 0, null);
		empStock.añadirProducto_nuevo("J", "Pandemic", "Juego cooperativo", "pandemic.jpg", 38.00, 6,
				tienda.seleccionarCategorias("Familiar"), 0, null, 0, 0, 0, 0, null, null, 2, 4, 8, 99, "Cooperativo");

		empStock.añadirProducto_nuevo("F", "Figura Darth Vader", "Figura de Star Wars", "vader.jpg", 49.99, 4,
				tienda.seleccionarCategorias("Replicas", "Ciencia-ficcion"), 0, null, 0, 25.0, 12.0, 10.0, "PVC",
				"Hasbro", 0, 0, 0, 0, null);

		empStock.añadirProducto_nuevo("F", "Figura Link", "Figura de Zelda", "link.jpg", 39.99, 7,
				tienda.seleccionarCategorias("Replicas", "Retro-Gaming"), 0, null, 0, 18.0, 10.0, 8.0, "PVC",
				"Nintendo", 0, 0, 0, 0, null);
		empStock.añadirProducto_nuevo("J", "Ticket to Ride", "Juego de trenes", "ttr.jpg", 42.00, 5,
				tienda.seleccionarCategorias("Familiar"), 0, null, 0, 0, 0, 0, null, null, 2, 5, 8, 99, "Estrategia");
		System.out.println("PROBAMOS CASO DE FALLO CON UN EMPLEADO QUE NO TIENE PERMISOS DE GESTION DE STOCK");

		empPedidos.añadirProducto_nuevo("C", "Akira Vol.1", "Manga de ciencia ficcion", "akira.jpg", 12.99, 20,
				tienda.seleccionarCategorias("Anime", "Ciencia-ficcion"), 350, "Kodansha", 1982, 0, 0, 0, null, null, 0,
				0, 0, 0, null);

		System.out.println("  Total productos en tienda: " + tienda.getStockVentas().size());
		for (ProductoVenta p : tienda.getStockVentas()) {
			System.out.println("  " + p.resumen());
		}
		System.out.println("REPONER STOCK:");
		ProductoVenta watchmen = tienda.buscarproductoPorNombre("Watchmen").get(0);
		empStock.reponerStockProducto(watchmen.getId(), 5);
		System.out.println("MODIFICAR LA DESCRIPCION DE UN PRODUCTO:");
		empStock.modificarDescripcionProducto(watchmen.getId(), "La obra maestra del noveno arte");

		System.out.println("AÑADIR PRODUCTOS A CATEGORIA:");
		empStock.añadirProductoACategoria(watchmen.getId(), "Retro-Gaming");
		empStock.añadirProductoACategoria(watchmen.getId(), "Familiar");
		System.out.println("  Categorias tras añadir:");
		watchmen.imprimirCategorias();
		System.out.println("ELIMINAR CATEGORIAS DE PRODUCTOS");
		empStock.eliminarProductoDeCategoria(watchmen.getId(), "Anime");
		System.out.println("  Categorias del producto tras eliminar Anime:");
		watchmen.imprimirCategorias();

		System.out.println("\n CREAR PACKS");

		ProductoVenta akira = tienda.buscarproductoPorNombre("Akira Vol.1").get(0);
		ProductoVenta catan = tienda.buscarproductoPorNombre("Catan").get(0);
		ProductoVenta pandemic = tienda.buscarproductoPorNombre("Pandemic").get(0);
		ProductoVenta ttr = tienda.buscarproductoPorNombre("Ticket to Ride").get(0);
		ProductoVenta figGoku = tienda.buscarproductoPorNombre("Figura Goku SSJ").get(0);
		ProductoVenta vader = tienda.buscarproductoPorNombre("Figura Darth Vader").get(0);
		ProductoVenta link = tienda.buscarproductoPorNombre("Figura Link").get(0);

		ArrayList<LineaPack> lineasGamer = construirLineasPack(new LineaPack(catan, 1), new LineaPack(figGoku, 1),
				new LineaPack(akira, 1));
		empStock.crearPack("Pack Gamer", "Pack con juego y figura", "pack.jpg", 70.00, 3, lineasGamer);
		Pack packGamer = tienda.buscarPackPorNombre("Pack Gamer");
		packGamer.resumenPrecios();
		imprimirStockPack(packGamer);
		// añadimos un producto que no estaba al pack(pandemic)
		System.out.println("\n  Añadir producto nuevo al pack (Pandemic):");
		boolean añadido = empStock.añadirProductoaPack(pandemic.getId(), packGamer.getId(), 1);
		System.out.println("  Resultado: " + (añadido ? "OK" : "FALLIDO"));
		imprimirStockPack(packGamer);

		// modificar unidades de producto que ya estaba (Akira)
		System.out.println("\n  Modificar unidades de Akira en el pack (de 1 a 2):");
		boolean modificado = empStock.modificarUnidadesProductoEnPack(akira.getId(), packGamer.getId(), 2);
		System.out.println("  Resultado: " + (modificado ? "OK" : "FALLIDO"));
		imprimirStockPack(packGamer);

		// modificar precio del pack
		empStock.modificarPrecioPack(packGamer.getId(), 65.00);
		System.out.println("\n  Precio actualizado:");
		packGamer.resumenPrecios();

		System.out.println("\n  Eliminar pandemic del pack:");
		boolean eliminadoProducto = empStock.eliminarProductoDePack(packGamer.getId(), pandemic.getId());
		System.out.println("  Resultado: " + (eliminadoProducto ? "OK" : "FALLIDO"));
		System.out.println("  Stock de pandemic recuperado: " + pandemic.getStockDisponible());
		imprimirStockPack(packGamer);

		System.out.println("\n  Intentar eliminar producto que no esta en el pack (Pandemic):");
		boolean eliminadoNoExiste = empStock.eliminarProductoDePack(packGamer.getId(), pandemic.getId());
		System.out.println("  Resultado: " + (eliminadoNoExiste ? "OK" : "FALLIDO - producto no estaba en el pack"));

		// Caso 6: eliminar el pack entero
		System.out.println("\n  Eliminar pack :");
		boolean eliminadoPack = empStock.eliminarPack(packGamer.getId());
		System.out.println("  Resultado: " + (eliminadoPack ? "OK" : "FALLIDO"));

		System.out.println("  Pack en tienda: "
				+ (tienda.buscarPackPorNombre("Pack Gamer") != null ? "SI" : "NO - eliminado correctamente"));

		System.out.println("\n REGISTRO Y LOGIN DE CLIENTES");
		tienda.registrarNuevoCliente("alice", "Alice@1234", "11111111A");
		tienda.registrarNuevoCliente("bob", "Bob@1234", "22222222B");
		tienda.registrarNuevoCliente("carlos", "Carlos@123", "33333333C");
		System.out.println("  Clientes registrados: " + tienda.obtenerClientesTienda().size());
		// Intentos fallidos
		System.out.println("\nIntentos con dni duplicado, nickname duplicado o contraseña poco segura:");
		tienda.registrarNuevoCliente("otro", "Otro@1234", "11111111A");
		tienda.registrarNuevoCliente("alice", "Alice@9999", "99999999Z");
		tienda.registrarNuevoCliente("dani", "hola", "22334455Y");
		tienda.registrarNuevoCliente("dani", "Lahyrfe3_", "444");

		System.out.println("Inicio de sesion Correcto: ");

		Cliente alice = tienda.loginCliente("alice", "Alice@1234");
		Cliente bob = tienda.loginCliente("bob", "Bob@1234");
		Cliente carlos = tienda.loginCliente("carlos", "Carlos@123");
		tienda.imprimirUsuariosConSesionActiva();

		// alice no tiene esa contraseña, luego no puede iniciar sesion
		System.out.println("Prueba a iniciar sesion con contraseña incorrecta: ");
		tienda.loginCliente("alice", "wrongpass");

		System.out.println("Modificacion del perfil:");
		alice.modificarPerfil("alicia", "Alicia@5678");

		System.out.println("Modificacion de perfil incorrecto(nickname ya utilizado por otro usuario):");
		alice.modificarPerfil("bob", "Bob@1234");

		System.out.println("\nPREFERENCIA DE NOTIFICACIONES: ");
		// Quitamos descuentos
		alice.configurarPreferenciaNotificacion(TipoNotificacion.DESCUENTO, false);

		// Añadir categorias de interes
		alice.añadirCategoriaInteresParaRecibirInfo("Anime");
		alice.añadirCategoriaInteresParaRecibirInfo("Accion");

		alice.verMisPreferencias();

		// Eliminar categoria de interes
		alice.eliminarCategoriaInteres("Accion");

		// Intentar desactivar notificacion obligatoria
		System.out.println("Intento de desactivar una notificacion que es obligatoria: ");
		boolean resultado = alice.configurarPreferenciaNotificacion(TipoNotificacion.PAGO_EXITOSO, false);

		System.out.println("\nFLUJO DE COMPRA Y BUSQUEDA: ");

		tienda.imprimirCatalogo();

		// Alice busca por nombre y compra
		System.out.println("\n  Alice busca 'watch':");
		alice.buscarProductosPorNombre("watch");
		System.out.println("\n  Alice busca 'catan':");
		alice.buscarProductosPorNombre("catan");

		alice.añadirProductoCarrito(watchmen, 1);
		alice.añadirProductoCarrito(catan, 1);
		System.out.println("  " + alice.getNickname() + " añade al carrito: '" + watchmen.getNombre() + "' y '"
				+ catan.getNombre() + "'");
		alice.reservarCarrito();
		Pedido pedidoAlice = alice.getHistorialPedidos().get(0);
		System.out.println("  Carrito reservado: pedido: " + pedidoAlice.getIdPedido() + " | total: "
				+ pedidoAlice.getTotal() + "€" + " | estado: " + pedidoAlice.getEstado());

		Date fechaTarjeta = new Date(System.currentTimeMillis() + 100000000L);
		boolean pagado = alice.pagarCarrito(pedidoAlice, "1234567890123456", fechaTarjeta, 123);
		System.out.println("  Pago -> " + (pagado ? "PAGADO" : "FALLIDO") + " | estado: " + pedidoAlice.getEstado());

		if (pagado) {
			empPedidos.prepararPedido(pedidoAlice.getIdPedido());
			alice.verHistorialPedidos();
			alice.solicitarRecogidaPedido(pedidoAlice.getCodigoRecogida());
			System.out.println("  Recogida solicitada: " + pedidoAlice.isRecogida_solicitada());
			empPedidos.entregarPedido(pedidoAlice.getCodigoRecogida());
			System.out.println("  Pedido entregado -> estado: " + pedidoAlice.getEstado());
			alice.escribirReseña(watchmen, 9, "Una obra maestra absoluta");
			System.out.println("  Reseña de '" + watchmen.getNombre() + "' -> media: " + watchmen.getMediaPuntuacion());
		} else {
			System.out.println("  Pago rechazado por el banco. Estado: " + pedidoAlice.getEstado());
		}

		// Bob busca por categoria y compra
		System.out.println("\n  Bob busca productos de la categoria 'Accion':");
		bob.buscarProductosPorCategoria("Accion");

		bob.añadirProductoCarrito(watchmen, 1);
		bob.reservarCarrito();
		Pedido pedidoBob = bob.getHistorialPedidos().get(0);
		System.out.println("  Pedido bob: " + pedidoBob.getIdPedido() + " | total: " + pedidoBob.getTotal() + "€"
				+ " | estado: " + pedidoBob.getEstado());

		boolean pagadoBob = bob.pagarCarrito(pedidoBob, "1234567890123456", fechaTarjeta, 123);
		System.out
				.println("  Pago bob -> " + (pagadoBob ? "PAGADO" : "FALLIDO") + " | estado: " + pedidoBob.getEstado());

		if (pagadoBob) {
			empPedidos.prepararPedido(pedidoBob.getIdPedido());
			bob.solicitarRecogidaPedido(pedidoBob.getCodigoRecogida());
			System.out.println("  Recogida solicitada: " + pedidoBob.isRecogida_solicitada());
			empPedidos.entregarPedido(pedidoBob.getCodigoRecogida());
			System.out.println("  Pedido entregado -> estado: " + pedidoBob.getEstado());
			bob.escribirReseña(watchmen, 7, "Muy bueno pero denso");
		} else {
			System.out.println("  Pago rechazado por el banco. Estado: " + pedidoBob.getEstado());
		}

		// Carlos busca con filtros y compra
		System.out.println("\n  Carlos busca por id el producto akira:");
		carlos.buscarProductoPorId(akira.getId());

		System.out.println("\n  Busqueda con filtros:");

		System.out.println("\n  Carlos filtra productos entre 10€ y 20€:");
		carlos.filtrarPorPrecio(10.0, 20.0);

		System.out.println("\n  Bob filtra productos de categoria 'Familiar':");
		bob.filtrarPorCategoria("Familiar");

		System.out.println("\n  Alice filtra productos con puntuacion minima 7:");
		alice.filtrarPorPuntuacion(7.0);

		System.out.println("\n  Carlos filtra Anime entre 10€ y 30€:");
		carlos.filtrarProductos(10.0, 30.0, 0.0, "Anime");

		System.out.println("\n  Bob filtra entre 10€ y 50€ con puntuacion minima 8:");
		bob.filtrarProductos(10.0, 50.0, 8.0);

		System.out.println("\n  Alice filtra Anime entre 10€ y 20€ con puntuacion minima 7:");
		alice.filtrarProductos(10.0, 20.0, 7.0, "Anime");

		System.out.println("\n  Carlos filtra con puntuacion minima 10 (sin resultados):");
		carlos.filtrarPorPuntuacion(10.0);

		System.out.println("\n  Bob filtra categoria inexistente:");
		bob.filtrarPorCategoria("CategoriaFalsa");

		// Carlos decide comprar akira tras buscarlo
		carlos.añadirProductoCarrito(akira, 2);
		carlos.reservarCarrito();
		Pedido pedidoCarlos = carlos.getHistorialPedidos().get(0);
		System.out.println("  Pedido carlos: " + pedidoCarlos.getIdPedido() + " | total: " + pedidoCarlos.getTotal()
				+ "€" + " | estado: " + pedidoCarlos.getEstado());

		boolean pagadoCarlos = carlos.pagarCarrito(pedidoCarlos, "1234567890123456", fechaTarjeta, 123);
		System.out.println(
				"  Pago carlos -> " + (pagadoCarlos ? "PAGADO" : "FALLIDO") + " | estado: " + pedidoCarlos.getEstado());

		if (pagadoCarlos) {
			empPedidos.prepararPedido(pedidoCarlos.getIdPedido());
			carlos.solicitarRecogidaPedido(pedidoCarlos.getCodigoRecogida());
			empPedidos.entregarPedido(pedidoCarlos.getCodigoRecogida());
			carlos.escribirReseña(akira, 8, "Imprescindible");
			System.out.println("  Carlos reseña '" + akira.getNombre() + "'");
		} else {
			System.out.println("  Pago rechazado por el banco. Estado: " + pedidoCarlos.getEstado());
		}

		alice.verReseñasProducto(watchmen);
		alice.verReseñasProducto(akira);

		System.out.println("\n  Stock tras las compras:");
		System.out.println("  " + watchmen.resumen());
		System.out.println("  " + akira.resumen());
		System.out.println("  " + catan.resumen());
		System.out.println("\n  Historial de pedidos:");
		alice.verHistorialPedidos();
		bob.verHistorialPedidos();
		carlos.verHistorialPedidos();

		System.out.println("\n PRODUCTOS DE SEGUNDA MANO");
		alice.subirProducto("Naruto Vol.1", "Manga en buen estado", "naruto.jpg");
		bob.subirProducto("Figura Pikachu", "Figura original sin caja", "pikachu.jpg");
		bob.subirProducto("Risk Edicion Especial", "Algo desgastado", "risk.jpg");
		carlos.subirProducto("One Piece Vol.5", "Buen estado", "op.jpg");
		System.out.println("\n  Carteras antes de tasar:");
		alice.verMiCartera();
		bob.verMiCartera();
		carlos.verMiCartera();
		Producto2Mano pAlice = alice.getCarteraIntercambio().get(0);
		Producto2Mano pBob1 = bob.getCarteraIntercambio().get(0);
		Producto2Mano pBob2 = bob.getCarteraIntercambio().get(1);
		Producto2Mano pCarlos = carlos.getCarteraIntercambio().get(0);
		boolean tasacionAlice = alice.solicitarTasacion(pAlice, "1111222233334444", 111, Date.valueOf("2029-01-01"));
		System.out.println("  Alice solicita tasar '" + pAlice.getNombre() + "': "
				+ (tasacionAlice ? "OK - pendiente de tasacion" : "FALLIDO - pago rechazado"));

		boolean tasacionBob1 = bob.solicitarTasacion(pBob1, "5555666677778888", 222, Date.valueOf("2030-06-01"));
		System.out.println("  Bob solicita tasar '" + pBob1.getNombre() + "': "
				+ (tasacionBob1 ? "OK - pendiente de tasacion" : "FALLIDO - pago rechazado"));

		boolean tasacionBob2 = bob.solicitarTasacion(pBob2, "5555666677778888", 222, Date.valueOf("2030-06-01"));
		System.out.println("  Bob solicita tasar '" + pBob2.getNombre() + "': "
				+ (tasacionBob2 ? "OK - pendiente de tasacion" : "FALLIDO - pago rechazado"));

		boolean tasacionCarlos = carlos.solicitarTasacion(pCarlos, "9999000011112222", 333, Date.valueOf("2031-12-01"));
		System.out.println("  Carlos solicita tasar '" + pCarlos.getNombre() + "': "
				+ (tasacionCarlos ? "OK - pendiente de tasacion" : "FALLIDO - pago rechazado"));

		System.out.println("  Pendientes de tasacion: " + tienda.getPendientesTasacion().size());

		// Casos de error en tasacion
		System.out.println("\n  Casos de error en la solicius de tasacion:");
		System.out.println("  Alice intenta tasar producto que no es suyo:");
		alice.solicitarTasacion(pBob1, "1111222233334444", 111, Date.valueOf("2029-01-01"));
		System.out.println("  Alice intenta tasar producto ya tasado:");
		alice.solicitarTasacion(pAlice, "1111222233334444", 111, Date.valueOf("2029-01-01"));
		alice.subirProducto("Dragon Ball Z Vol.1", "Como nuevo", "dbz.jpg");
		Producto2Mano pAlice2 = alice.getCarteraIntercambio().get(1);
		System.out.println("  Alice intenta tasar con tarjeta caducada:");
		alice.solicitarTasacion(pAlice2, "1111222233334444", 111, Date.valueOf("2020-01-01"));

		alice.solicitarTasacion(pAlice2, "1111222233334444", 111, Date.valueOf("2029-01-01"));

		System.out.println("\n  Empleado tasa los productos pendientes:");

		empTasador.tasarProducto(pAlice.getId(), 20.0, EstadoProducto.MUY_BUENO);
		empTasador.tasarProducto(pBob1.getId(), 15.0, EstadoProducto.PERFECTO);
		empTasador.tasarProducto(pBob2.getId(), 10.0, EstadoProducto.USO_LIGERO);
		empTasador.tasarProducto(pCarlos.getId(), 12.0, EstadoProducto.MUY_BUENO);

		System.out.println("\n  Carteras tras tasar:");
		alice.verMiCartera();
		bob.verMiCartera();
		carlos.verMiCartera();

		empTasador.tasarProducto(pAlice2.getId(), 18.0, EstadoProducto.PERFECTO);
		System.out.println("\n Funciones de busqueda productos de segunda mano:");
		System.out.println("\n  Alice busca todos los productos de segunda mano:");
		alice.buscarProductosSegundaMano();

		System.out.println("\n  Bob busca por nombre 'figura':");
		bob.buscarProducto2ManoNombre("figura");

		System.out.println("\n  Carlos busca por id '" + pAlice.getId() + "':");
		carlos.buscarProducto2ManoPorid(pAlice.getId());

		System.out.println("\n  Alice filtra segunda mano entre 10€ y 18€ de valor estimado:");
		alice.filtrar2ManoPorValor(10.0, 18.0);

		System.out.println("\n  Bob filtra segunda mano con estado minimo MUY_BUENO:");
		bob.filtrar2ManoPorEstado(EstadoProducto.MUY_BUENO);

		System.out.println("\n  Carlos filtra segunda mano entre 10€ y 20€ con estado minimo PERFECTO:");
		carlos.filtrar2Mano(10.0, 20.0, EstadoProducto.PERFECTO);

		System.out.println("\n  Alice filtra con estado minimo USO_LIGERO (sin resultados esperados):");
		alice.filtrar2ManoPorEstado(EstadoProducto.USO_LIGERO);

		System.out.println("\n  Alice ve la cartera de bob:");
		alice.verCarteraCliente("bob");

		System.out.println("\n  Alice intenta ver su propia cartera con este metodo:");
		alice.verCarteraCliente("alice");

		System.out.println("INTERCAMBIOS: ");

		boolean ofertaCreada = alice.proponerOferta(bob, alice.crearListaProductos2Mano(pAlice, pAlice2),
				alice.crearListaProductos2Mano(pBob1, pBob2));
		System.out.println("  Oferta creada: " + (ofertaCreada ? "OK" : "FALLIDA"));

		Oferta oferta = alice.getOfertasPendientes().get(0);
		oferta.imprimirResumen();

		System.out.println("\n  Casos de error en ofertas:");
		System.out.println("  Alice intenta proponerse oferta a si misma:");
		alice.proponerOferta(alice, alice.crearListaProductos2Mano(pAlice, pAlice2),
				alice.crearListaProductos2Mano(pBob1, pBob2));

		System.out.println("  Alice intenta ofertar productos ya bloqueados:");
		alice.proponerOferta(bob, alice.crearListaProductos2Mano(pAlice, pAlice2),
				alice.crearListaProductos2Mano(pBob1, pBob2));
		// Bob acepta
		System.out.println("\n  Bob acepta la oferta:");
		bob.confirmarIntercambio(oferta);
		System.out.println("  Estado tras aceptar: " + oferta.getEstado());

		// Intentar aceptar oferta ya aceptada
		System.out.println("  Bob intenta aceptar la oferta otra vez:(Ya fue aceptada, probamos la excepcion)");
		bob.confirmarIntercambio(oferta);
		System.out.println("\n  Empleado ejecuta el intercambio:");
		boolean intercambiado = empTasador.confirmarIntercambio(oferta);
		System.out.println(
				"  Resultado: " + (intercambiado ? "REALIZADO" : "FALLIDO") + " | estado: " + oferta.getEstado());
		System.out.println(
				"\nUna vez que se realiza el intercambio los productos se han borrado de las carteras de los usuarios del intercambio.");
		alice.verMiCartera();
		bob.verMiCartera();
		System.out.println("\n  Historial de intercambios tras ejecutar:");
		alice.verMiHistorialIntercambios();
		bob.verMiHistorialIntercambios();

		System.out.println("\n Subimos mas productos");
		bob.subirProducto("Carta Yu-Gi-Oh", "Holografica rara", "yugioh.jpg");
		Producto2Mano pBobNuevo = bob.getCarteraIntercambio().get(bob.getCarteraIntercambio().size() - 1);

		boolean tasacionBobNuevo = bob.solicitarTasacion(pBobNuevo, "5555666677778888", 222,
				Date.valueOf("2030-06-01"));
		System.out.println(
				"  Bob solicita tasar '" + pBobNuevo.getNombre() + "': " + (tasacionBobNuevo ? "OK" : "FALLIDO"));

		empTasador.tasarProducto(pBobNuevo.getId(), 8.0, EstadoProducto.MUY_BUENO);

		carlos.subirProducto("Guia Pokemon", "Completa y en buen estado", "guia.jpg");
		Producto2Mano pCarlos2 = carlos.getCarteraIntercambio().get(carlos.getCarteraIntercambio().size() - 1);

		boolean tasacionCarlos2 = carlos.solicitarTasacion(pCarlos2, "9999000011112222", 333,
				Date.valueOf("2031-12-01"));
		System.out.println(
				"  Carlos solicita tasar '" + pCarlos2.getNombre() + "': " + (tasacionCarlos2 ? "OK" : "FALLIDO"));

		empTasador.tasarProducto(pCarlos2.getId(), 6.0, EstadoProducto.MUY_BUENO);
		boolean ofertaBobCarlos = bob.proponerOferta(carlos, bob.crearListaProductos2Mano(pBobNuevo),
				carlos.crearListaProductos2Mano(pCarlos, pCarlos2));
		System.out.println("  Oferta creada: " + (ofertaBobCarlos ? "OK" : "FALLIDA"));
		Oferta ofertaRechazada = bob.getOfertasPendientes().get(0);
		ofertaRechazada.imprimirResumen();
		System.out.println("\n  Ofertas enviadas de bob:");
		bob.verMisOfertasEnviadas();
		System.out.println("\n  Ofertas por responder de carlos:");
		carlos.verMisOfertasPorResponder();

		// Carlos rechaza
		System.out.println("\n  Carlos rechaza la oferta de bob:");
		carlos.eliminarOfertadeOfertasPendientes(ofertaRechazada);
		System.out.println("  Estado: " + ofertaRechazada.getEstado());
		System.out.println("  " + pBobNuevo.getNombre() + " desbloqueado: " + !pBobNuevo.isBloqueado());
		System.out.println("  Pendientes bob: " + bob.getOfertasPendientes().size());
		System.out.println("  Pendientes carlos: " + carlos.getOfertasPendientes().size());
		System.out.println("Probamos que los productos no se han eliminado de las carteras de los usuarios");
		carlos.verCarteraCliente("bob");
		carlos.verMiCartera();

		// Ver intercambios con un cliente concreto
		System.out.println("\n  Alice ve sus intercambios con bob:");
		List<Oferta> intercambiosAliceBob = alice.verIntercambioscon(bob);
		System.out.println("  Resultado: " + intercambiosAliceBob.size() + " intercambio(s)");
		for (Oferta o : intercambiosAliceBob) {
			o.imprimirResumen();
		}

		// Ver intercambios con alguien con quien no ha intercambiado
		System.out.println("\n  Alice ve sus intercambios con carlos (ninguno):");
		List<Oferta> intercambiosAliceCarlos = alice.verIntercambioscon(carlos);
		System.out.println("  Resultado: " + intercambiosAliceCarlos.size() + " intercambio(s)");
		// Crear descuentos en orden de prioridad
		System.out.println("\n  Creando descuentos por orden de prioridad:");
		System.out.println("\n  Creando descuentos:");

		boolean d1 = gestor.crearDescuentoVolumen("Descuento Verano", 50.0, 10.0, LocalDateTime.now().minusMinutes(1),
				LocalDateTime.now().plusHours(2));
		System.out.println("  Descuento volumen (>50€ -> 10%): " + d1);
		boolean d2 = gestor.crearDescuentoCategoria("Descuento Anime", "Anime", 15.0,
				LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(2));
		System.out.println("  Descuento categoria Anime (15%): " + d2);

		boolean d3 = gestor.crearDescuentoCantidad("Descuento Cantidad", akira.getId(), 3, 20.0,
				LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(2));
		System.out.println("  Descuento cantidad (3+ unidades -> 20%): " + d3);
		boolean d4 = gestor.crearDescuentoRegalo("Regalo Figura Link", link.getId(), 40.0,
				LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(2));
		System.out.println("  Descuento regalo (>40€ -> figura link gratis): " + d4);
		tienda.imprimirDescuentosActivos();

		// - Alice compra mas de 50€ -> aplica volumen
		System.out.println("\n  Caso 1 - Alice compra mas de 50€ (watchmen + catan = 60€):");
		alice.añadirProductoCarrito(watchmen, 1);
		alice.añadirProductoCarrito(catan, 1);
		alice.getCarritoActual().imprimirCarrito();
		System.out.println("  Vaciando carrito:");
		alice.getCarritoActual().vaciarCarrito();
		alice.getCarritoActual().imprimirCarrito();
		// Caso 2 - Bob compra Anime menos de 50€ -> aplica categoria
		System.out.println("\n   Bob compra Anime menos de 50€ (akira = 12.99€):");
		bob.añadirProductoCarrito(akira, 1);
		bob.getCarritoActual().imprimirCarrito();
		System.out.println("  Vaciando carrito:");
		bob.getCarritoActual().vaciarCarrito();
		bob.getCarritoActual().imprimirCarrito();

		// Caso 3 - Carlos compra 3 akiras -> aplica cantidad
		System.out.println("\n  Carlos compra 3 akiras -> aplica Anime (antes que Cantidad en lista):");
		carlos.añadirProductoCarrito(akira, 3);
		carlos.getCarritoActual().imprimirCarrito();
		System.out.println("  Vaciando carrito:");
		carlos.getCarritoActual().vaciarCarrito();
		carlos.getCarritoActual().imprimirCarrito();
		// Caso 3b - Alice compra 3 watchmen -> no aplica volumen (45€ < 50€),
		// no aplica Anime (no es de Anime), aplica Cantidad
		System.out.println("\n  Caso 3b - Alice compra 3 watchmen (45€, no es Anime):");
		System.out.println("  No aplica volumen (<50€), no aplica Anime, aplica Cantidad:");
		alice.añadirProductoCarrito(watchmen, 3);
		alice.getCarritoActual().imprimirCarrito();
		System.out.println("  Vaciando carrito:");
		alice.getCarritoActual().vaciarCarrito();
		alice.getCarritoActual().imprimirCarrito();

		// Caso 4 - Alice compra entre 40€ y 50€ -> aplica regalo
		System.out.println("\n  Caso 4 - Alice compra entre 40€ y 50€ (catan = 45€) -> regalo:");
		System.out.println("  Stock figura link antes: " + link.getStockDisponible());
		alice.añadirProductoCarrito(catan, 1);
		alice.getCarritoActual().imprimirCarrito();
		System.out.println("  Vaciando carrito:");
		alice.getCarritoActual().vaciarCarrito();
		alice.getCarritoActual().imprimirCarrito();

		// Caso 4 - Alice compra entre 40€ y 50€ -> aplica regalo
		System.out.println("\n  Caso 4 - Alice compra entre 40€ y 50€ (catan = 45€) -> regalo:");
		System.out.println("  Stock figura link antes: " + link.getStockDisponible());
		alice.añadirProductoCarrito(catan, 1);
		alice.getCarritoActual().imprimirCarrito();
		alice.reservarCarrito();
		Pedido pedidoRegalo = alice.getHistorialPedidos().get(alice.getHistorialPedidos().size() - 1);
		System.out.println("  Stock figura link tras reservar: " + link.getStockDisponible());

		boolean pagadoRegalo = alice.pagarCarrito(pedidoRegalo, "1111222233334444", Date.valueOf("2029-01-01"), 111);
		System.out.println(
				"  Pago -> " + (pagadoRegalo ? "PAGADO" : "FALLIDO") + " | estado: " + pedidoRegalo.getEstado());

		if (pagadoRegalo) {
			empPedidos.prepararPedido(pedidoRegalo.getIdPedido());

			alice.solicitarRecogidaPedido(pedidoRegalo.getCodigoRecogida());
			empPedidos.entregarPedido(pedidoRegalo.getCodigoRecogida());
			System.out.println("  Stock figura link tras entregar: " + link.getStockDisponible());
		}

		System.out.println("  Historial de pedidos de alice:");
		alice.verHistorialPedidos();

		System.out.println("\n Bob compra menos de 20€ sin categoria con descuento (watchmen = 15€):");
		bob.añadirProductoCarrito(watchmen, 1);
		bob.getCarritoActual().imprimirCarrito();
		System.out.println("  Vaciando carrito:");
		bob.getCarritoActual().vaciarCarrito();
		bob.getCarritoActual().imprimirCarrito();

		// Limpiar caducados y eliminar
		System.out.println("\n  Crear descuento caducado:");
		gestor.crearDescuentoVolumen("Descuento Caducado", 30.0, 5.0, LocalDateTime.now().minusHours(2),
				LocalDateTime.now().minusMinutes(1));
		tienda.imprimirDescuentosActivos();

		System.out.println("\n  Historial completo (el caducado si debe aparecer):");
		tienda.imprimirHistorialDescuentos();
		System.out.println("\n  Limpiar descuentos caducados:");
		tienda.limpiarDescuentosCaducados();
		tienda.imprimirDescuentosActivos();

		bob.verMiCartera();
		// Crear descuento que expira en 2 segundos
		System.out.println("\n  Crear descuento que expira en 2 segundos:");
		gestor.crearDescuentoVolumen("Descuento Efimero", 30.0, 5.0, LocalDateTime.now().minusMinutes(1),
				LocalDateTime.now().plusSeconds(2));
		tienda.imprimirDescuentosActivos(); // aparece como activo

		System.out.println("  Esperando 3 segundos...");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("  Descuentos activos sin limpiar (el efimero sigue en lista pero no activo):");
		tienda.imprimirDescuentosActivos();

		System.out.println("  Limpiar caducados:");
		tienda.limpiarDescuentosCaducados();
		tienda.imprimirDescuentosActivos();
/*
		System.out.println("SIMULACION DE CADUCIDAD DE TIEMPOS:");
		// ── Carrito caducado ──────────────────────────────────────────────────────
		System.out.println("\n  Configuramos tiempo max carrito a 1 minuto:");
		gestor.setTiempoMaxCarrito(1);
		System.out.println("  Tiempos -> Carrito: " + tienda.getTiempoMaxCarrito() + "min | Oferta: "
				+ tienda.getTiempoMaxOferta() + "min | Pago: " + tienda.getTiempoMaxPago() + "min");
		System.out.println("  Alice añade watchmen al carrito:");
		alice.añadirProductoCarrito(watchmen, 1);
		alice.getCarritoActual().imprimirCarrito();
		System.out.println("  Stock watchmen antes de caducar: " + watchmen.getStockDisponible());

		System.out.println("  Esperando 61 segundos para que caduque el carrito...");
		try {
			Thread.sleep(61000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("  Intentando reservar carrito caducado:");
		boolean reservado = alice.reservarCarrito();
		System.out.println("  Resultado: " + (reservado ? "OK" : "BLOQUEADO - carrito caducado"));
		System.out.println("  Stock watchmen recuperado: " + watchmen.getStockDisponible());

		gestor.setTiempoMaxCarrito(60);
		System.out.println("  Tiempo max carrito restaurado: " + tienda.getTiempoMaxCarrito() + "min");
		// ── Pedido caducado ───────────────────────────────────────────────────────
		System.out.println("\n  Configuramos tiempo max pago a 1 minuto:");
		gestor.setTiempoMaxPago(1);
		System.out.println("  Tiempos -> Carrito: " + tienda.getTiempoMaxCarrito() + "min | Oferta: "
				+ tienda.getTiempoMaxOferta() + "min | Pago: " + tienda.getTiempoMaxPago() + "min");

		System.out.println("  Bob crea un pedido:");
		bob.añadirProductoCarrito(watchmen, 1);
		bob.reservarCarrito();
		Pedido pedidoCaducado = bob.getHistorialPedidos().get(bob.getHistorialPedidos().size() - 1);
		System.out.println("  Pedido: " + pedidoCaducado.getIdPedido() + " | estado: " + pedidoCaducado.getEstado()
				+ " | stock watchmen: " + watchmen.getStockDisponible());

		System.out.println("  Esperando 61 segundos para que caduque el pedido...");
		try {
			Thread.sleep(61000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("  Intentando pagar pedido caducado:");
		boolean pagadoCaducado = bob.pagarCarrito(pedidoCaducado, "5555666677778888", Date.valueOf("2030-06-01"), 222);
		System.out.println("  Resultado: " + (pagadoCaducado ? "PAGADO" : "BLOQUEADO - pedido caducado") + " | estado: "
				+ pedidoCaducado.getEstado());
		System.out.println("  Stock watchmen recuperado: " + watchmen.getStockDisponible());

		gestor.setTiempoMaxPago(30);
		System.out.println("  Tiempo max pago restaurado: " + tienda.getTiempoMaxPago() + "min");
		// ── Oferta caducada ───────────────────────────────────────────────────────
		System.out.println("\n  Configuramos tiempo max oferta a 1 minuto:");
		gestor.setTiempoMaxOferta(1);
		System.out.println("  Tiempos -> Carrito: " + tienda.getTiempoMaxCarrito() + "min | Oferta: "
				+ tienda.getTiempoMaxOferta() + "min | Pago: " + tienda.getTiempoMaxPago() + "min");

		carlos.subirProducto("Digimon Vol.1", "Buen estado", "digimon.jpg");
		Producto2Mano pCarlosNuevo = carlos.getCarteraIntercambio().get(carlos.getCarteraIntercambio().size() - 1);
		boolean tasacionNueva = carlos.solicitarTasacion(pCarlosNuevo, "9999000011112222", 333,
				Date.valueOf("2031-12-01"));
		empTasador.tasarProducto(pCarlosNuevo.getId(), 8.0, EstadoProducto.MUY_BUENO);

		// Alice sube producto nuevo para la prueba de oferta caducada
		alice.subirProducto("Bleach Vol.1", "Como nuevo", "bleach.jpg");
		Producto2Mano pAliceNuevo = alice.getCarteraIntercambio().get(alice.getCarteraIntercambio().size() - 1);
		alice.solicitarTasacion(pAliceNuevo, "1111222233334444", 111, Date.valueOf("2029-01-01"));
		empTasador.tasarProducto(pAliceNuevo.getId(), 10.0, EstadoProducto.MUY_BUENO);

		boolean ofertaCaducadaCreada = carlos.proponerOferta(alice, carlos.crearListaProductos2Mano(pCarlosNuevo),
				alice.crearListaProductos2Mano(pAliceNuevo));

		if (!ofertaCaducadaCreada) {
			System.out.println("  La oferta no se pudo crear.");
		} else {
			Oferta ofertaCaducada = carlos.getOfertasPendientes().get(carlos.getOfertasPendientes().size() - 1);
			ofertaCaducada.imprimirResumen();

			System.out.println("  Esperando 61 segundos para que caduque la oferta...");
			try {
				Thread.sleep(61000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println("  Caducada: " + ofertaCaducada.haCaducado());
			System.out.println("  Alice intenta aceptar oferta caducada:");
			alice.confirmarIntercambio(ofertaCaducada);
			System.out.println("  Estado: " + ofertaCaducada.getEstado());
		}

		gestor.setTiempoMaxOferta(30);
		System.out.println("  Tiempo max oferta restaurado: " + tienda.getTiempoMaxOferta() + "min");
		gestor.configurarTiemposSistema(60, 30, 30);
		System.out.println("  Tiempos restaurados -> Carrito: " + tienda.getTiempoMaxCarrito() + "min | Oferta: "
				+ tienda.getTiempoMaxOferta() + "min | Pago: " + tienda.getTiempoMaxPago() + "min");
*/
		System.out.println("\nESTADISTICAS");
		System.out.println("\n  Rankings de clientes:");
		gestor.verClientesTopCompras();
		gestor.verClientesTopIntercambios();
		gestor.verClientesConMasPedidosCancelados();

		System.out.println("\n  Ingresos:");
		gestor.consultarIngresosVenta();
		gestor.consultarIngresosTasacion();
		

		try {
			gestor.consultarIngresosRango(LocalDate.now().withDayOfYear(1), LocalDate.now());
			gestor.consultarIngresosPorMesesActual();

			
			System.out.println("\n  Intentar consultar rango invalido:");
			try {
				gestor.consultarIngresosRango(LocalDate.now(), LocalDate.now().minusDays(10));
			} catch (RangoFechasInvalidoException e) {
				System.out.println("  Error capturado: " + e.getMessage());
			}

			// Caso error - año invalido
			System.out.println("\n  Intentar consultar año invalido (-1):");
			try {
				gestor.consultarIngresosPorMeses(-1);
			} catch (AnioInvalidoException e) {
				System.out.println("  Error capturado: " + e.getMessage());
			}

		} catch (RangoFechasInvalidoException e) {
			System.out.println("  Error rango: " + e.getMessage());
		} catch (AnioInvalidoException e) {
			System.out.println("  Error año: " + e.getMessage());
		}
	}
}
