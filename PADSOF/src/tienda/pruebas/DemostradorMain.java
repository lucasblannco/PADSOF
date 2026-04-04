package pruebas;

import java.util.*;
import java.sql.Date;
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

	}

}
